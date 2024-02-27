/** whatever is needed to make capacitor work */

var SafeArea = Capacitor.Plugins.SafeArea;
var PushNotifications = Capacitor.Plugins.PushNotifications;
var Network = Capacitor.Plugins.Network;

var LocalNotifications = Capacitor.Plugins.LocalNotifications;

var CapacitorApp = Capacitor.Plugins.App;


// Prepared & separated this function for now as we have a configuration specific to iOS (see capacitor.config.js#21) although this will push all UI elements(bg, buttons etc) to the safe area content and will leave
// the background design to be pushed as well
// But once we need some code logic to perform certain fix for overlapping buttons or navigation bars then we will be using this.
async function ExecSafeArea($){
    var insets = await SafeArea.getSafeAreaInsets()
    console.log('SafeArea called...', insets);
    SafeArea.getSafeAreaInsets().then(({ insets }) => {
      console.log(insets);
    });

    SafeArea.getStatusBarHeight().then(({ statusBarHeight }) => {
      console.log(statusBarHeight, 'statusbarHeight');
    });

    await SafeArea.removeAllListeners();
    // when safe-area changed
    await SafeArea.addListener('safeAreaChanged', data => {
      const { insets } = data;
      for (const [key, value] of Object.entries(insets)) {
        console.log("Safe Area Changed: Data " , key , value);
//        document.documentElement.style.setProperty(
//          `--safe-area-${key}`,
//          `${value}px`,
//        );
      }
    });
}

async function LinkCapacitor($, identityName) {
  // this function is probably required because adding just the plugin itself will do the job but won't minimize the app.
  // TODO: to test iOS devices
  if (Capacitor.getPlatform() === 'android'){
    CapacitorApp.addListener("backButton", ({ canGoBack }) => {
      // TODO : add logger
      console.log("BackButton called ... ", canGoBack);
      if (!canGoBack) {
        CapacitorApp.minimizeApp();
      } else {
        window.history.back();
      }
    });
  }

  // handles email deep linking or any types of url links that points to our app launcher
  // this only works on simple format(e.g, www.google.com/search?/page) the target for this are the sub-folders, paths and pages
  CapacitorApp.addListener("appUrlOpen", (data) => {
    // TODO : add logger
    console.log("App opened with URL:", data);
    const url = new URL(data.url);
    // if the user has not added a host, grab it from appUrlOpen and use it
    if (!localStorage.getItem("mdo_host")) {
      $.registerManifest(`${url.origin}/~d/.product-manifest.json`, true);
    }
    if (navigation.length > 1) {
      window.rxhtml.goto(url.pathname, true);
    }
  });

  PushNotifications.requestPermissions().then(result => {
    $.bump("nps"); // setup
    if (result.receive === 'granted') {
      console.log("granted push permission")
      $.bump("npg");
      $.setPushStatus("granted");
      PushNotifications.register();
    } else {
      console.log("failed push permission request")
      $.setPushStatus("failed");
      $.bump("npf1");
    }
  }, function () {
    console.log("explicit failed push request")
    $.setPushStatus("failed");
    $.bump("npf2");
  });

  PushNotifications.addListener('registration', function (token) {
    console.log("got push token:" + token.value);
    $.afterHaveIdentity(identityName, function(identity) {
      $.connection.IdentityHash(identity, {
        success: function(result) {
          var pushTokenKey = "push_token_" + result.identityHash;
          var sub = {};
          sub['@method'] = 'capacitor';
          sub['@time'] = new Date().getTime();
          sub.token = token.value;
          var device = {};
          device.mode = 'native'
          if (window && window.navigator && window.navigator.userAgent) {
            device.ua = window.navigator.userAgent;
          }
          var val = localStorage.getItem(pushTokenKey);
          if (val && val == token.value) {
            $.bump("npa"); // ready
            $.setPushStatus("success");
            return;
          }
          $.connection.PushRegister(identity, $.domain, sub, device, {
            success: function() {
              $.bump("npr"); // ready
              $.setPushStatus("success");
              localStorage.setItem(pushTokenKey, token.value);
            },
            failure: function() {
              $.bump("npf3");
            }
          });
        },
        failure: function() {
          $.bump("npf5");
        }
      });
    });
  });

  // Some issue with our setup and push will not work
  PushNotifications.addListener('registrationError', function (error) {
    console.log(error);
    $.bump("npf4");
    window.rxhtml.setPushStatus("failed");
  });

  // Show us the notification payload if the app is open on our device
  PushNotifications.addListener('pushNotificationReceived', function (notification) {
    console.log('Push received: ' + JSON.stringify(notification));
    // TODO: get the tracking id from the notification, call into RxHTML to trigger a logging message
    if (Capacitor.getPlatform() === 'android'){
        LocalNotifications.schedule({
          notifications: [
            {
              id: notification.id,
              title: notification.title,
              body: notification.body,
              data: notification.data
            }
          ]
        });
    }

  });

  // Method called when tapping on a notification
  PushNotifications.addListener('pushNotificationActionPerformed', function (action) {
    console.log('Push action performed: ' + JSON.stringify(action));
    $.bump("npap");
    if (action.notification.data.url) {
      window.rxhtml.goto(action.notification.data.url, true);
    }
  });

  Network.addListener('networkStatusChange', status => {
    // TODO: pump this into RxHTML
  });

  let status = await Network.getStatus();

  // TODO: feed into RxHTML
  console.log("Starting...");
  console.log(status);
  console.log("rxcap ready...");
}
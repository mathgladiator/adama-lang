/** whatever is needed to make capacitor work */
var PushNotifications = Capacitor.Plugins.PushNotifications;
var Network = Capacitor.Plugins.Network;

async function LinkCapacitor($, identityName) {
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
          if (val && val == token) {
            $.bump("npa"); // ready
            $.setPushStatus("success");
            return;
          }
          $.connection.PushRegister(identity, $.domain, sub, device, {
            success: function() {
              $.bump("npr"); // ready
              $.setPushStatus("success");
              localStorage.setItem(pushTokenKey, token);
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
    // TODO: get the tracking id from the notification, call into RxHTML to trigger a logging message
    console.log('Push received: ' + JSON.stringify(notification));
  });

// Method called when tapping on a notification
  PushNotifications.addListener('pushNotificationActionPerformed', function (action) {
    // TODO: feed the URL into RxHTML
    console.log('Push action performed: ' + JSON.stringify(action));
  });

  Network.addListener('networkStatusChange', (status) => {
    // TODO: pump this into RxHTML
  });

  let status = await Network.getStatus();
// TODO: feed into RxHTML
  console.log("Starting...");
  console.log(status);
  console.log("rxcap ready...");
}
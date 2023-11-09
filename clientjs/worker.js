/* Adama worker: handle simple push notifications */

console.log("adama worker started");
self.addEventListener('push', function(event) {
  if (event.data) {
    var json = event.data.json();
    if (json) {
      console.log(json);
      var pushToken = "NA";
      if ('@pt' in json) {
        pushToken = json['@pt'];
      }
      var options = {
        body: json.body ? json.body : "Generic Push Body",
      };
      if (json.icon) {
        options.icon = json.icon;
      }
      var show = true;
      if (json.hide) {
        show = false;
      }
      var data = {};
      if (json.url) {
        data.url = json.url;
      }
      options.data = data;
      if (show) {
        event.waitUntil(self.registration.showNotification(json.title ? json.title : "Generic Push Title", options));
      }
      if (typeof(json.badge) == "number" && navigator.setAppBadge) {
        try {
          event.waitUntil(navigator.setAppBadge(json.badge));
        } catch (e) {}
      }
      try {
        event.waitUntil(fetch(self.location.protocol + "//" + self.location.host + "/~pt/" + pushToken, {
          method: "PUT",
          headers: {},
          body: pushToken
        }));
      } catch (ex) {
        console.log(ex);
        // don't care
      }
    }
  }
});

self.addEventListener('notificationclick', function(event) {
  if (event.notification && event.notification.data && event.notification.data.url) {
    var url = self.location.protocol + "//" + self.location.host + event.notification.data.url;
    var home = self.location.protocol + "//" + self.location.host;
    event.notification.close(); // Android needs explicit close.
    event.waitUntil(
      clients.matchAll({type: 'window'}).then( wc => {
        // Check if there is already a window/tab open with the target URL
        var clientWithPrefix = null;
        var clientWithHome = null;
        for (var i = 0; i < wc.length; i++) {
          var client = wc[i];
          console.log("client:" + client.url + ";" + url);
          if (client.url === url && 'focus' in client) {
            return client.focus();
          }
          if (client.url.startsWith(url) && 'navigate' in client) {
            clientWithPrefix = client;
          }
          if (client.url.startsWith(home) && 'navigate' in client) {
            clientWithHome = client;
          }
        }
        if (clientWithPrefix != null) {
          console.log("nav:prefix:" + url);
          return clientWithPrefix.navigate(url);
        }
        if (clientWithHome != null) {
          console.log("nav:home:" + url);
          return clientWithHome.navigate(url);
        }
        if (clients.openWindow) {
          console.log("open:new:" + url);
          return clients.openWindow(url);
        }
      })
    );
  }
});
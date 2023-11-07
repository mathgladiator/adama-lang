/* Adama worker: handle simple push notifications */
self.addEventListener('push', function(event) {
  if (event.data) {
    var json = event.data.json();
    if (json) {
      var options = {
        body: json.body ? json.body : "Generic Push Body",
      };
      if (json.icon) {
        options.icon = json.icon;
      }
      event.waitUntil(self.registration.showNotification(json.title ? json.title : "Generic Push Title", options));

      if (json.badge) {
        if (navigator.setAppBadge) {
          try {
            navigator.setAppBadge(json.badge);
          } catch (e) {}
        }
      }

    }
  }
});
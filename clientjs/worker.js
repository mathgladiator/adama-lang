/* Adama worker: handle simple push notifications */
self.addEventListener('push', function(event) {
  if (event.data) {
    var json = event.data.json();
    if (json) {
      var options = {
        body: json.body ? json.body : "Generic Push Body",
      };
      event.waitUntil(self.registration.showNotification(json.title ? json.title : "Generic Push Title", options));
    }
  }
});
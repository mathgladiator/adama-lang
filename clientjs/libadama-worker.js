self.addEventListener("push",function(t){var i,e;t.data&&(i=t.data.json())&&(e={body:i.body||"Generic Push Body"},t.waitUntil(self.registration.showNotification(i.title||"Generic Push Title",e)))});

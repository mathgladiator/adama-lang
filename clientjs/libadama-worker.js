self.addEventListener("push",function(i){var t,n;i.data&&(t=i.data.json())&&(n={body:t.body||"Generic Push Body"},t.icon&&(n.icon=t.icon),i.waitUntil(self.registration.showNotification(t.title||"Generic Push Title",n)))});
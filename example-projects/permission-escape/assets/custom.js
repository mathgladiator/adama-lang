window.rxhtml.PRWP('checkbox_hierarchy', function (dom, state, rxobj, maker, framework) {
  // produce the children
  maker(dom, state);

  // we are going to index the children DOM into a logical structure to provide the behavior semantics that we want
  var permission_groups = {next:0};

  // various changes will require a re-sync, and this will bring the DOM consistent
  var sync = function() {
    for (var gid = 1; gid <= permission_groups.next; gid++) {
      var grp = permission_groups[gid];
      var c_true = 0;
      var c_false = 0;
      for (var k = 0; k < grp.children.length; k++) {
        if (grp.children[k].checked) {
          c_true++;
        } else {
          c_false++;
        }
      }
      grp.all.checked = c_false == 0;
      grp.summary.innerHTML = c_true + "/" + (c_true + c_false);
    }
  };

  var index_aspects = function(dom, grp) {
    if (dom.hasAttribute('group:header')) {
      grp.toggle = dom;
      dom.addEventListener('click', function() {
        if (this.target.style.display == "none") {
          this.target.style.display = "";
        } else {
          this.target.style.display = "none";
        }
      }.bind(grp));
    }
    if (dom.hasAttribute("group:summary")) {
      grp.summary = dom;
    }
    if (dom.hasAttribute('group:body')) {
      grp.target = dom;
      dom.addEventListener('click', function() {
      }.bind(grp));
    }
    if (dom.hasAttribute("group:all")) {
      grp.all = dom;
      dom.addEventListener('change', function() {
        for (var k = 0; k < this.children.length; k++) {
          this.children[k].checked = this.all.checked;
        }
        sync();
      }.bind(grp));
    }
    if (dom.hasAttribute("group:child")) {
      grp.children.push(dom);
      dom.addEventListener('change', function() {
        sync();
      }.bind(grp));
      dom.addEventListener('forced', function() {
        sync();
      });
    }

    var new_grp = grp;
    if (dom.tagName.toUpperCase() == "PERMISSION-GROUP") {
      new_grp = {children:[]};
      permission_groups.next++;
      permission_groups[permission_groups.next] = new_grp;
      console.log("WR:" + permission_groups.next);
    }
    if ("children" in dom) {
      var arr = dom.children;
      var n = arr.length;
      for (var k = 0; k < n; k++) {
        index_aspects(dom.children[k], new_grp);
      }
    }
  };

  index_aspects(dom, null);

  sync();
});
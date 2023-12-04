window.rxhtml.PRWP('checkbox_hierarchy', function (dom, state, rxobj, maker, framework) {
  // produce the children
  maker(dom, state);

  // we are going to index the children DOM into a logical structure to provide the behavior semantics that we want
  var permission_groups = {};

  // since everything is indexed by a concept of a group, this function will get[orCreate] the group structure
  var getGroup = function(name) {
    if (!(name in permission_groups)) {
      permission_groups[name] = {name:name, children:[]};
    }
    return permission_groups[name];
  }

  // various changes will require a re-sync, and this will bring the DOM consistent
  var sync = function() {
    for(var gid in permission_groups) {
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
      if (c_true == 0) {
        grp.all.checked = false;
      }
      if (c_false == 0) {
        grp.all.checked = true;
      }
      grp.summary.innerHTML = c_true + "/" + (c_true + c_false);
    }
  };

  var index_aspects = function(dom) {
    if (dom.hasAttribute('showhide:toggle')) {
      var grp = getGroup(dom.getAttribute('showhide:toggle'));
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
      var grp = getGroup(dom.getAttribute('group:summary'));
      grp.summary = dom;
    }
    if (dom.hasAttribute('showhide:target')) {
      var grp = getGroup(dom.getAttribute('showhide:target'));
      grp.target = dom;
      dom.addEventListener('click', function() {
      }.bind(grp));
    }
    if (dom.hasAttribute("check:group")) {
      var grp = getGroup(dom.getAttribute('check:group'));
      grp.all = dom;
      dom.addEventListener('change', function() {
        for (var k = 0; k < this.children.length; k++) {
          this.children[k].checked = this.all.checked;
        }
        sync();
      }.bind(grp));
    }
    if (dom.hasAttribute("part:of")) {
      var grp = getGroup(dom.getAttribute('part:of'));
      grp.children.push(dom);
      dom.addEventListener('change', function() {
        sync();
      }.bind(grp));
      // TODO: RxHTML needs a forcechange event
    }

    if ("children" in dom) {
      var arr = dom.children;
      var n = arr.length;
      for (var k = 0; k < n; k++) {
        index_aspects(dom.children[k]);
      }
    }
  };

  index_aspects(dom);

  sync();
});
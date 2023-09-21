/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.rxhtml;

public class TemplateFormWarningsActionsTests extends BaseRxHtmlTest {
  @Override
  public boolean dev() {
    return false;
  }
  @Override
  public String issues() {
    StringBuilder issues = new StringBuilder();
    issues.append("WARNING:/:The input 'x' is excessive.");
    issues.append("\nWARNING:/:Failed to find an input for 'email'");
    issues.append("\nWARNING:/:Failed to find an input for 'password'");
    issues.append("\nWARNING:/:The input 'x' is excessive.");
    issues.append("\nWARNING:/:The input 'email' is excessive.");
    issues.append("\nWARNING:/:Failed to find an input for 'password'");
    issues.append("\nWARNING:/:Failed to find an input for 'key'");
    issues.append("\nWARNING:/:Failed to find an input for 'email'");
    issues.append("\nWARNING:/:Failed to find an input for 'password'");
    issues.append("\nWARNING:/:Failed to find an input for 'code'");
    issues.append("\nWARNING:/:Emails should have type 'email'.");
    issues.append("\nWARNING:/:Passwords should have type 'password' or 'hidden'.");
    issues.append("\nWARNING:/:The input 'email' is excessive.");
    issues.append("\nWARNING:/:Passwords should have type 'password' or 'hidden'.");
    issues.append("\nWARNING:/:Failed to find an input for 'username'");
    issues.append("\nWARNING:/:Failed to find an input for 'space'");
    issues.append("\nWARNING:/:Failed to find an input for 'key'");
    issues.append("\nWARNING:/:Failed to find an input for 'username'");
    issues.append("\nWARNING:/:Failed to find an input for 'password'");
    issues.append("\nWARNING:/:Failed to find an input for 'new_password'");
    issues.append("\nWARNING:/:Failed to find an input for 'space'");
    issues.append("\nWARNING:/:Failed to find an input for 'key'");
    issues.append("\nWARNING:/:Failed to find an input for 'username'");
    issues.append("\nWARNING:/:Failed to find an input for 'password'");
    issues.append("\nWARNING:/:Failed to find an input for 'new_password'");
    issues.append("\nWARNING:/:Emails should have type 'email'");
    issues.append("\nWARNING:/:Emails should have type 'email'");
    issues.append("\nWARNING:/:Passwords should have type 'password'.");
    issues.append("\nWARNING:/:Failed to find an input for 'space'");
    issues.append("\nWARNING:/:Failed to find an input for 'key'");
    issues.append("\nWARNING:/:Failed to find an input for 'files'");
    return issues.toString();
  }
  @Override
  public String gold() {
    StringBuilder gold = new StringBuilder();
    gold.append("JavaScript:(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    b.append($.T(' Simple Page '));");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.rx_forward='/';");
    gold.append("\n    $.aSO(c,a,'default',d);");
    gold.append("\n    var e=[];");
    gold.append("\n    e.push($.bS(c,$.pV(a),'sign_in_failed',false));");
    gold.append("\n    $.onB(c,'success',a,e);");
    gold.append("\n    var f=[];");
    gold.append("\n    f.push($.bS(c,$.pV(a),'sign_in_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,f);");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    $.SA(g,'name',\"x\");");
    gold.append("\n    c.append(g);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var g=$.RX([]);");
    gold.append("\n    g.rx_forward='/w00t';");
    gold.append("\n    $.aDSO(c,a,'default',g);");
    gold.append("\n    var h=[];");
    gold.append("\n    h.push($.bS(c,$.pV(a),'docnope',false));");
    gold.append("\n    $.onB(c,'success',a,h);");
    gold.append("\n    var i=[];");
    gold.append("\n    i.push($.bS(c,$.pV(a),'docnope',true));");
    gold.append("\n    $.onB(c,'failure',a,i);");
    gold.append("\n    var j = $.E('input');");
    gold.append("\n    $.SA(j,'type',\"hidden\");");
    gold.append("\n    $.SA(j,'name',\"space\");");
    gold.append("\n    j.value=\"s \";");
    gold.append("\n    c.append(j);");
    gold.append("\n    var j = $.E('input');");
    gold.append("\n    $.SA(j,'type',\"hidden\");");
    gold.append("\n    $.SA(j,'name',\"x\");");
    gold.append("\n    j.value=\"k\";");
    gold.append("\n    c.append(j);");
    gold.append("\n    var j = $.E('input');");
    gold.append("\n    $.SA(j,'name',\"username\");");
    gold.append("\n    $.SA(j,'type',\"username\");");
    gold.append("\n    c.append(j);");
    gold.append("\n    var j = $.E('input');");
    gold.append("\n    $.SA(j,'name',\"email\");");
    gold.append("\n    $.SA(j,'type',\"password\");");
    gold.append("\n    c.append(j);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aCC(c,a,'foo');");
    gold.append("\n    var j=[];");
    gold.append("\n    j.push($.bS(c,$.pV(a),'sign_in_failed',false));");
    gold.append("\n    $.onB(c,'success',a,j);");
    gold.append("\n    var k=[];");
    gold.append("\n    k.push($.bS(c,$.pV(a),'sign_in_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,k);");
    gold.append("\n    var l = $.E('input');");
    gold.append("\n    $.SA(l,'name',\"x\");");
    gold.append("\n    c.append(l);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSU(c,a,'/');");
    gold.append("\n    var l=[];");
    gold.append("\n    l.push($.bS(c,$.pV(a),'sign_up_failed',false));");
    gold.append("\n    $.onB(c,'success',a,l);");
    gold.append("\n    var m=[];");
    gold.append("\n    m.push($.bS(c,$.pV(a),'sign_up_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,m);");
    gold.append("\n    var n = $.E('input');");
    gold.append("\n    $.SA(n,'name',\"x\");");
    gold.append("\n    c.append(n);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSP(c,a,'/');");
    gold.append("\n    var n=[];");
    gold.append("\n    n.push($.bS(c,$.pV(a),'set_password_failed',false));");
    gold.append("\n    $.onB(c,'success',a,n);");
    gold.append("\n    var o=[];");
    gold.append("\n    o.push($.bS(c,$.pV(a),'set_password_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,o);");
    gold.append("\n    var p = $.E('input');");
    gold.append("\n    $.SA(p,'name',\"x\");");
    gold.append("\n    c.append(p);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSD(c,a,'channel');");
    gold.append("\n    var p=[];");
    gold.append("\n    p.push($.bS(c,$.pV(a),'send_failed',false));");
    gold.append("\n    $.onB(c,'success',a,p);");
    gold.append("\n    var q=[];");
    gold.append("\n    q.push($.bS(c,$.pV(a),'send_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,q);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var r=$.RX([]);");
    gold.append("\n    r.rx_forward='/';");
    gold.append("\n    $.aSO(c,a,'default',r);");
    gold.append("\n    var s=[];");
    gold.append("\n    s.push($.bS(c,$.pV(a),'sign_in_failed',false));");
    gold.append("\n    $.onB(c,'success',a,s);");
    gold.append("\n    var t=[];");
    gold.append("\n    t.push($.bS(c,$.pV(a),'sign_in_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,t);");
    gold.append("\n    var u = $.E('input');");
    gold.append("\n    $.SA(u,'name',\"email\");");
    gold.append("\n    c.append(u);");
    gold.append("\n    var u = $.E('input');");
    gold.append("\n    $.SA(u,'name',\"password\");");
    gold.append("\n    c.append(u);");
    gold.append("\n    var u = $.E('input');");
    gold.append("\n    $.SA(u,'type',\"submit\");");
    gold.append("\n    c.append(u);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var u=$.RX([]);");
    gold.append("\n    u.rx_forward='/';");
    gold.append("\n    $.aDSO(c,a,'default',u);");
    gold.append("\n    var v=[];");
    gold.append("\n    v.push($.bS(c,$.pV(a),'sign_in_failed',false));");
    gold.append("\n    $.onB(c,'success',a,v);");
    gold.append("\n    var w=[];");
    gold.append("\n    w.push($.bS(c,$.pV(a),'sign_in_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,w);");
    gold.append("\n    var x = $.E('input');");
    gold.append("\n    $.SA(x,'name',\"email\");");
    gold.append("\n    c.append(x);");
    gold.append("\n    var x = $.E('input');");
    gold.append("\n    $.SA(x,'name',\"password\");");
    gold.append("\n    $.SA(x,'type',\"text\");");
    gold.append("\n    c.append(x);");
    gold.append("\n    var x = $.E('input');");
    gold.append("\n    $.SA(x,'type',\"submit\");");
    gold.append("\n    c.append(x);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var x=$.RX([]);");
    gold.append("\n    x.rx_forward='/';");
    gold.append("\n    $.aDSOr(c,a,'default',x);");
    gold.append("\n    var y=[];");
    gold.append("\n    y.push($.bS(c,$.pV(a),'sign_in_failed',false));");
    gold.append("\n    $.onB(c,'success',a,y);");
    gold.append("\n    var z=[];");
    gold.append("\n    z.push($.bS(c,$.pV(a),'sign_in_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,z);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var ab=$.RX([]);");
    gold.append("\n    ab.rx_forward='/';");
    gold.append("\n    $.adDSOr(c,a,'default',ab);");
    gold.append("\n    var bb=[];");
    gold.append("\n    bb.push($.bS(c,$.pV(a),'sign_in_failed',false));");
    gold.append("\n    $.onB(c,'success',a,bb);");
    gold.append("\n    var cb=[];");
    gold.append("\n    cb.push($.bS(c,$.pV(a),'sign_in_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,cb);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aCC(c,a,'foo');");
    gold.append("\n    var db=[];");
    gold.append("\n    db.push($.bS(c,$.pV(a),'sign_in_failed',false));");
    gold.append("\n    $.onB(c,'success',a,db);");
    gold.append("\n    var eb=[];");
    gold.append("\n    eb.push($.bS(c,$.pV(a),'sign_in_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,eb);");
    gold.append("\n    var fb = $.E('input');");
    gold.append("\n    $.SA(fb,'type',\"submit\");");
    gold.append("\n    c.append(fb);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSU(c,a,'/');");
    gold.append("\n    var fb=[];");
    gold.append("\n    fb.push($.bS(c,$.pV(a),'sign_up_failed',false));");
    gold.append("\n    $.onB(c,'success',a,fb);");
    gold.append("\n    var gb=[];");
    gold.append("\n    gb.push($.bS(c,$.pV(a),'sign_up_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,gb);");
    gold.append("\n    var hb = $.E('input');");
    gold.append("\n    $.SA(hb,'name',\"email\");");
    gold.append("\n    c.append(hb);");
    gold.append("\n    var hb = $.E('input');");
    gold.append("\n    $.SA(hb,'type',\"submit\");");
    gold.append("\n    c.append(hb);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSP(c,a,'/');");
    gold.append("\n    var hb=[];");
    gold.append("\n    hb.push($.bS(c,$.pV(a),'set_password_failed',false));");
    gold.append("\n    $.onB(c,'success',a,hb);");
    gold.append("\n    var ib=[];");
    gold.append("\n    ib.push($.bS(c,$.pV(a),'set_password_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,ib);");
    gold.append("\n    var jb = $.E('input');");
    gold.append("\n    $.SA(jb,'name',\"email\");");
    gold.append("\n    c.append(jb);");
    gold.append("\n    var jb = $.E('input');");
    gold.append("\n    $.SA(jb,'name',\"password\");");
    gold.append("\n    c.append(jb);");
    gold.append("\n    var jb = $.E('input');");
    gold.append("\n    $.SA(jb,'name',\"code\");");
    gold.append("\n    c.append(jb);");
    gold.append("\n    var jb = $.E('input');");
    gold.append("\n    $.SA(jb,'type',\"submit\");");
    gold.append("\n    c.append(jb);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSD(c,a,'channel');");
    gold.append("\n    var jb=[];");
    gold.append("\n    jb.push($.bS(c,$.pV(a),'send_failed',false));");
    gold.append("\n    $.onB(c,'success',a,jb);");
    gold.append("\n    var kb=[];");
    gold.append("\n    kb.push($.bS(c,$.pV(a),'send_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,kb);");
    gold.append("\n    var lb = $.E('input');");
    gold.append("\n    $.SA(lb,'name',\"param\");");
    gold.append("\n    c.append(lb);");
    gold.append("\n    var lb = $.E('input');");
    gold.append("\n    $.SA(lb,'type',\"submit\");");
    gold.append("\n    c.append(lb);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var lb=$.RX([]);");
    gold.append("\n    lb.rx_forward=true;");
    gold.append("\n    $.aUP(c,a,'default',lb);");
    gold.append("\n    var mb=[];");
    gold.append("\n    mb.push($.bS(c,$.pV(a),'asset_upload_failed',false));");
    gold.append("\n    $.onB(c,'success',a,mb);");
    gold.append("\n    var nb=[];");
    gold.append("\n    nb.push($.bS(c,$.pV(a),'asset_upload_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,nb);");
    gold.append("\n    var ob = $.E('input');");
    gold.append("\n    $.SA(ob,'name',\"spacex\");");
    gold.append("\n    c.append(ob);");
    gold.append("\n    var ob = $.E('input');");
    gold.append("\n    $.SA(ob,'name',\"keyx\");");
    gold.append("\n    c.append(ob);");
    gold.append("\n    b.append(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\nStyle:");
    gold.append("\nShell:<!DOCTYPE html>");
    gold.append("\n<html>");
    gold.append("\n<head><script src=\"https://aws-us-east-2.adama-platform.com/libadama.js\"></script><script>");
    gold.append("\n");
    gold.append("\n(function($){");
    gold.append("\n  $.PG(['fixed',''], function(b,a) {");
    gold.append("\n    b.append($.T(' Simple Page '));");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var d=$.RX([]);");
    gold.append("\n    d.rx_forward='/';");
    gold.append("\n    $.aSO(c,a,'default',d);");
    gold.append("\n    var e=[];");
    gold.append("\n    e.push($.bS(c,$.pV(a),'sign_in_failed',false));");
    gold.append("\n    $.onB(c,'success',a,e);");
    gold.append("\n    var f=[];");
    gold.append("\n    f.push($.bS(c,$.pV(a),'sign_in_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,f);");
    gold.append("\n    var g = $.E('input');");
    gold.append("\n    $.SA(g,'name',\"x\");");
    gold.append("\n    c.append(g);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var g=$.RX([]);");
    gold.append("\n    g.rx_forward='/w00t';");
    gold.append("\n    $.aDSO(c,a,'default',g);");
    gold.append("\n    var h=[];");
    gold.append("\n    h.push($.bS(c,$.pV(a),'docnope',false));");
    gold.append("\n    $.onB(c,'success',a,h);");
    gold.append("\n    var i=[];");
    gold.append("\n    i.push($.bS(c,$.pV(a),'docnope',true));");
    gold.append("\n    $.onB(c,'failure',a,i);");
    gold.append("\n    var j = $.E('input');");
    gold.append("\n    $.SA(j,'type',\"hidden\");");
    gold.append("\n    $.SA(j,'name',\"space\");");
    gold.append("\n    j.value=\"s \";");
    gold.append("\n    c.append(j);");
    gold.append("\n    var j = $.E('input');");
    gold.append("\n    $.SA(j,'type',\"hidden\");");
    gold.append("\n    $.SA(j,'name',\"x\");");
    gold.append("\n    j.value=\"k\";");
    gold.append("\n    c.append(j);");
    gold.append("\n    var j = $.E('input');");
    gold.append("\n    $.SA(j,'name',\"username\");");
    gold.append("\n    $.SA(j,'type',\"username\");");
    gold.append("\n    c.append(j);");
    gold.append("\n    var j = $.E('input');");
    gold.append("\n    $.SA(j,'name',\"email\");");
    gold.append("\n    $.SA(j,'type',\"password\");");
    gold.append("\n    c.append(j);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aCC(c,a,'foo');");
    gold.append("\n    var j=[];");
    gold.append("\n    j.push($.bS(c,$.pV(a),'sign_in_failed',false));");
    gold.append("\n    $.onB(c,'success',a,j);");
    gold.append("\n    var k=[];");
    gold.append("\n    k.push($.bS(c,$.pV(a),'sign_in_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,k);");
    gold.append("\n    var l = $.E('input');");
    gold.append("\n    $.SA(l,'name',\"x\");");
    gold.append("\n    c.append(l);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSU(c,a,'/');");
    gold.append("\n    var l=[];");
    gold.append("\n    l.push($.bS(c,$.pV(a),'sign_up_failed',false));");
    gold.append("\n    $.onB(c,'success',a,l);");
    gold.append("\n    var m=[];");
    gold.append("\n    m.push($.bS(c,$.pV(a),'sign_up_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,m);");
    gold.append("\n    var n = $.E('input');");
    gold.append("\n    $.SA(n,'name',\"x\");");
    gold.append("\n    c.append(n);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSP(c,a,'/');");
    gold.append("\n    var n=[];");
    gold.append("\n    n.push($.bS(c,$.pV(a),'set_password_failed',false));");
    gold.append("\n    $.onB(c,'success',a,n);");
    gold.append("\n    var o=[];");
    gold.append("\n    o.push($.bS(c,$.pV(a),'set_password_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,o);");
    gold.append("\n    var p = $.E('input');");
    gold.append("\n    $.SA(p,'name',\"x\");");
    gold.append("\n    c.append(p);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSD(c,a,'channel');");
    gold.append("\n    var p=[];");
    gold.append("\n    p.push($.bS(c,$.pV(a),'send_failed',false));");
    gold.append("\n    $.onB(c,'success',a,p);");
    gold.append("\n    var q=[];");
    gold.append("\n    q.push($.bS(c,$.pV(a),'send_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,q);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var r=$.RX([]);");
    gold.append("\n    r.rx_forward='/';");
    gold.append("\n    $.aSO(c,a,'default',r);");
    gold.append("\n    var s=[];");
    gold.append("\n    s.push($.bS(c,$.pV(a),'sign_in_failed',false));");
    gold.append("\n    $.onB(c,'success',a,s);");
    gold.append("\n    var t=[];");
    gold.append("\n    t.push($.bS(c,$.pV(a),'sign_in_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,t);");
    gold.append("\n    var u = $.E('input');");
    gold.append("\n    $.SA(u,'name',\"email\");");
    gold.append("\n    c.append(u);");
    gold.append("\n    var u = $.E('input');");
    gold.append("\n    $.SA(u,'name',\"password\");");
    gold.append("\n    c.append(u);");
    gold.append("\n    var u = $.E('input');");
    gold.append("\n    $.SA(u,'type',\"submit\");");
    gold.append("\n    c.append(u);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var u=$.RX([]);");
    gold.append("\n    u.rx_forward='/';");
    gold.append("\n    $.aDSO(c,a,'default',u);");
    gold.append("\n    var v=[];");
    gold.append("\n    v.push($.bS(c,$.pV(a),'sign_in_failed',false));");
    gold.append("\n    $.onB(c,'success',a,v);");
    gold.append("\n    var w=[];");
    gold.append("\n    w.push($.bS(c,$.pV(a),'sign_in_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,w);");
    gold.append("\n    var x = $.E('input');");
    gold.append("\n    $.SA(x,'name',\"email\");");
    gold.append("\n    c.append(x);");
    gold.append("\n    var x = $.E('input');");
    gold.append("\n    $.SA(x,'name',\"password\");");
    gold.append("\n    $.SA(x,'type',\"text\");");
    gold.append("\n    c.append(x);");
    gold.append("\n    var x = $.E('input');");
    gold.append("\n    $.SA(x,'type',\"submit\");");
    gold.append("\n    c.append(x);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var x=$.RX([]);");
    gold.append("\n    x.rx_forward='/';");
    gold.append("\n    $.aDSOr(c,a,'default',x);");
    gold.append("\n    var y=[];");
    gold.append("\n    y.push($.bS(c,$.pV(a),'sign_in_failed',false));");
    gold.append("\n    $.onB(c,'success',a,y);");
    gold.append("\n    var z=[];");
    gold.append("\n    z.push($.bS(c,$.pV(a),'sign_in_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,z);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var ab=$.RX([]);");
    gold.append("\n    ab.rx_forward='/';");
    gold.append("\n    $.adDSOr(c,a,'default',ab);");
    gold.append("\n    var bb=[];");
    gold.append("\n    bb.push($.bS(c,$.pV(a),'sign_in_failed',false));");
    gold.append("\n    $.onB(c,'success',a,bb);");
    gold.append("\n    var cb=[];");
    gold.append("\n    cb.push($.bS(c,$.pV(a),'sign_in_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,cb);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aCC(c,a,'foo');");
    gold.append("\n    var db=[];");
    gold.append("\n    db.push($.bS(c,$.pV(a),'sign_in_failed',false));");
    gold.append("\n    $.onB(c,'success',a,db);");
    gold.append("\n    var eb=[];");
    gold.append("\n    eb.push($.bS(c,$.pV(a),'sign_in_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,eb);");
    gold.append("\n    var fb = $.E('input');");
    gold.append("\n    $.SA(fb,'type',\"submit\");");
    gold.append("\n    c.append(fb);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSU(c,a,'/');");
    gold.append("\n    var fb=[];");
    gold.append("\n    fb.push($.bS(c,$.pV(a),'sign_up_failed',false));");
    gold.append("\n    $.onB(c,'success',a,fb);");
    gold.append("\n    var gb=[];");
    gold.append("\n    gb.push($.bS(c,$.pV(a),'sign_up_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,gb);");
    gold.append("\n    var hb = $.E('input');");
    gold.append("\n    $.SA(hb,'name',\"email\");");
    gold.append("\n    c.append(hb);");
    gold.append("\n    var hb = $.E('input');");
    gold.append("\n    $.SA(hb,'type',\"submit\");");
    gold.append("\n    c.append(hb);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSP(c,a,'/');");
    gold.append("\n    var hb=[];");
    gold.append("\n    hb.push($.bS(c,$.pV(a),'set_password_failed',false));");
    gold.append("\n    $.onB(c,'success',a,hb);");
    gold.append("\n    var ib=[];");
    gold.append("\n    ib.push($.bS(c,$.pV(a),'set_password_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,ib);");
    gold.append("\n    var jb = $.E('input');");
    gold.append("\n    $.SA(jb,'name',\"email\");");
    gold.append("\n    c.append(jb);");
    gold.append("\n    var jb = $.E('input');");
    gold.append("\n    $.SA(jb,'name',\"password\");");
    gold.append("\n    c.append(jb);");
    gold.append("\n    var jb = $.E('input');");
    gold.append("\n    $.SA(jb,'name',\"code\");");
    gold.append("\n    c.append(jb);");
    gold.append("\n    var jb = $.E('input');");
    gold.append("\n    $.SA(jb,'type',\"submit\");");
    gold.append("\n    c.append(jb);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    $.aSD(c,a,'channel');");
    gold.append("\n    var jb=[];");
    gold.append("\n    jb.push($.bS(c,$.pV(a),'send_failed',false));");
    gold.append("\n    $.onB(c,'success',a,jb);");
    gold.append("\n    var kb=[];");
    gold.append("\n    kb.push($.bS(c,$.pV(a),'send_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,kb);");
    gold.append("\n    var lb = $.E('input');");
    gold.append("\n    $.SA(lb,'name',\"param\");");
    gold.append("\n    c.append(lb);");
    gold.append("\n    var lb = $.E('input');");
    gold.append("\n    $.SA(lb,'type',\"submit\");");
    gold.append("\n    c.append(lb);");
    gold.append("\n    b.append(c);");
    gold.append("\n    var c = $.E('form');");
    gold.append("\n    var lb=$.RX([]);");
    gold.append("\n    lb.rx_forward=true;");
    gold.append("\n    $.aUP(c,a,'default',lb);");
    gold.append("\n    var mb=[];");
    gold.append("\n    mb.push($.bS(c,$.pV(a),'asset_upload_failed',false));");
    gold.append("\n    $.onB(c,'success',a,mb);");
    gold.append("\n    var nb=[];");
    gold.append("\n    nb.push($.bS(c,$.pV(a),'asset_upload_failed',true));");
    gold.append("\n    $.onB(c,'failure',a,nb);");
    gold.append("\n    var ob = $.E('input');");
    gold.append("\n    $.SA(ob,'name',\"spacex\");");
    gold.append("\n    c.append(ob);");
    gold.append("\n    var ob = $.E('input');");
    gold.append("\n    $.SA(ob,'name',\"keyx\");");
    gold.append("\n    c.append(ob);");
    gold.append("\n    b.append(c);");
    gold.append("\n  });");
    gold.append("\n})(RxHTML);");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n</script><style>");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n");
    gold.append("\n</style></head><body></body><script>RxHTML.init();</script></html>");
    return gold.toString();
  }
  @Override
  public String source() {
    StringBuilder source = new StringBuilder();
    source.append("<forest>");
    source.append("\n    <page uri=\"/\">");
    source.append("\n        Simple Page");
    source.append("\n        <form rx:action=\"adama:sign-in\">");
    source.append("\n            <input name=\"x\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"document:sign-in\" rx:failure-variable=\"docnope\" rx:forward=\"/w00t\">");
    source.append("\n            <input type=\"hidden\" name=\"space\" value=\"s \"/>");
    source.append("\n            <input type=\"hidden\" name=\"x\" value=\"k\" />");
    source.append("\n            <input name=\"username\" type=\"username\" />");
    source.append("\n            <input name=\"email\" type=\"password\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"custom:foo\">");
    source.append("\n            <input name=\"x\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:sign-up\">");
    source.append("\n            <input name=\"x\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:set-password\">");
    source.append("\n            <input name=\"x\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"send:channel\">");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:sign-in\">");
    source.append("\n            <input name=\"email\" />");
    source.append("\n            <input name=\"password\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"document:sign-in\">");
    source.append("\n            <input name=\"email\" />");
    source.append("\n            <input name=\"password\" type=\"text\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"document:sign-in-reset\">");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"domain:sign-in-reset\">");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"custom:foo\">");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:sign-up\">");
    source.append("\n            <input name=\"email\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:set-password\">");
    source.append("\n            <input name=\"email\" />");
    source.append("\n            <input name=\"password\" />");
    source.append("\n            <input name=\"code\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"send:channel\">");
    source.append("\n            <input name=\"param\" />");
    source.append("\n            <input type=\"submit\" />");
    source.append("\n        </form>");
    source.append("\n        <form rx:action=\"adama:upload-asset\">");
    source.append("\n            <input name=\"spacex\" />");
    source.append("\n            <input name=\"keyx\" />");
    source.append("\n        </form>");
    source.append("\n    </page>");
    source.append("\n</forest>");
    return source.toString();
  }
}

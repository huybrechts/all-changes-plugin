/*
 * The MIT License
 *
 * Copyright (c) 2011, Stefan Wolf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.jenkinsci.plugins.all_changes.AllChangesBuildAction

import com.google.common.collect.Multimap
import hudson.Functions
import hudson.model.AbstractBuild
import hudson.model.AbstractBuild.DependencyChange
import hudson.scm.ChangeLogSet
import org.jvnet.localizer.LocaleProvider

import java.text.DateFormat

f = namespace(lib.FormTagLib)
l = namespace(lib.LayoutTagLib)
t = namespace("/lib/hudson")
st = namespace("jelly:stapler")

def build = my.build

t.summary(icon: "orange-square.png") {
    Multimap<ChangeLogSet.Entry, AbstractBuild> changes = my.getAllChanges();
    if (changes.empty) {
        text(_("No dependency changes."))
    }
    p {
        text(_("Changes in dependencies"))
    }
    ol() {
      for (entry in changes.keySet()) {
        li() {
          showEntry(entry, build, changes.get(entry))
        }
      }
    }
}

private def showEntry(entry, AbstractBuild build, Collection<AbstractBuild> builds) {
  showChangeSet(entry)
  boolean firstDrawn = false
  for (AbstractBuild b in builds) {
    if (b != build) {
      if (!firstDrawn) {
        text(" (")
        firstDrawn = true
      }
      else {
        text(", ")
      }
      a(href: "${rootURL}/${b.project.url}") {text(b.project.displayName)}
      st.nbsp()
      a(href: "${rootURL}/${b.url}") {
        text(b.displayName)
      }
    }
  }
  if (firstDrawn) {
    text(")")
  }
}

private def showChangeSet(ChangeLogSet.Entry c) {
  def build = c.parent.build
  def browser = build.project.scm.effectiveBrowser
  raw(c.msgAnnotated)
  raw(" &#8212; ")
  if (browser?.getChangeSetLink(c)) {
    a(href: browser.getChangeSetLink(c), _("detail"))
  } else {
    a(href: "${build.absoluteUrl}changes", _("detail"))
  }
}

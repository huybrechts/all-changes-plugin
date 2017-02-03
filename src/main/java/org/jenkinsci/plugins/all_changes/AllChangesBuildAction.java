package org.jenkinsci.plugins.all_changes;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.InvisibleAction;
import hudson.scm.ChangeLogSet;

import java.util.Set;

public class AllChangesBuildAction extends InvisibleAction {

    private final AbstractBuild build;

    public AllChangesBuildAction(AbstractBuild build) {
        this.build = build;
    }

    public AbstractBuild getBuild() {
        return build;
    }

    public Multimap<ChangeLogSet.Entry, AbstractBuild> getAllChanges() {
        Set<AbstractBuild> builds = new AllChangesAction(build.getProject()).getContributingBuilds(build);
        builds.remove(build);
        Multimap<String, ChangeLogSet.Entry> changes = ArrayListMultimap.create();
        for (AbstractBuild changedBuild : builds) {
            ChangeLogSet<ChangeLogSet.Entry> changeSet = changedBuild.getChangeSet();
            for (ChangeLogSet.Entry entry : changeSet) {
                changes.put(entry.getCommitId() + entry.getMsgAnnotated() + entry.getTimestamp(), entry);
            }
        }
        Multimap<ChangeLogSet.Entry, AbstractBuild> change2Build = HashMultimap.create();
        for (String changeKey : changes.keySet()) {
            ChangeLogSet.Entry change = changes.get(changeKey).iterator().next();
            for (ChangeLogSet.Entry entry : changes.get(changeKey)) {
                change2Build.put(change, entry.getParent().build);
            }
        }
        return change2Build;
    }


}

package org.jenkinsci.plugins.all_changes;

import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.FreeStyleBuild;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

@Extension
public class AllChangesRunListener extends RunListener<AbstractBuild> {

    @Override
    public void onStarted(AbstractBuild build, TaskListener listener) {
        if (build instanceof FreeStyleBuild && !((FreeStyleBuild) build).getProject().getUpstreamProjects().isEmpty()) {
            build.addAction(new AllChangesBuildAction(build));
        }
    }
}

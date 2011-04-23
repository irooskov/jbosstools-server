package org.jboss.ide.eclipse.as.test.publishing.v2;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModuleFile;
import org.jboss.ide.eclipse.as.core.publishers.AbstractPublishMethod;
import org.jboss.ide.eclipse.as.core.publishers.PublishUtil;
import org.jboss.ide.eclipse.as.core.server.xpl.PublishCopyUtil;
import org.jboss.ide.eclipse.as.core.server.xpl.PublishCopyUtil.IPublishCopyCallbackHandler;

public class MockPublishMethod extends AbstractPublishMethod {

	public static final String PUBLISH_METHOD_ID = "mock";
	public static ArrayList<IPath> changed = new ArrayList<IPath>();
	public static ArrayList<IPath> removed = new ArrayList<IPath>();

	public IPublishCopyCallbackHandler getCallbackHandler(IPath path,
			IServer server) {
		return new MockCopyCallbackHandler(path);
	}

	public String getPublishDefaultRootFolder(IServer server) {
		return "/";
	}

	public String getPublishMethodId() {
		return PUBLISH_METHOD_ID;
	}
	public static void reset() {
		changed.clear();
		removed.clear();
	}
	public static IPath[] getRemoved() {
		return (IPath[]) removed.toArray(new IPath[removed.size()]);
	}

	public static IPath[] getChanged() {
		return (IPath[]) changed.toArray(new IPath[changed.size()]);
	}


	public class MockCopyCallbackHandler implements IPublishCopyCallbackHandler {
		private IPath root;
		public MockCopyCallbackHandler(IPath root) {
			this.root = root;
		}
		
		public IStatus[] deleteResource(IPath path, IProgressMonitor monitor)
				throws CoreException {
			IPath path2 = root.append(path);
			if( !removed.contains(path2.makeRelative()))
				removed.add(path2.makeRelative());
			return new IStatus[]{};
		}

		public IStatus[] makeDirectoryIfRequired(IPath dir,
				IProgressMonitor monitor) throws CoreException {
			IPath path2 = root.append(dir);
			if( !changed.contains(path2.makeRelative()))
				changed.add(path2.makeRelative());
			return new IStatus[]{};
		}

		private boolean shouldRestartModule = false;
		public boolean shouldRestartModule() {
			return shouldRestartModule;
		}
		public IStatus[] copyFile(IModuleFile mf, IPath path,
				IProgressMonitor monitor) throws CoreException {
			File file = PublishUtil.getFile(mf);
			shouldRestartModule |= PublishCopyUtil.checkRestartModule(file);
			IPath path2 = root.append(path);
			if( !changed.contains(path2.makeRelative()))
				changed.add(path2.makeRelative());
			return new IStatus[]{};
		}
		
		public IStatus[] touchResource(IPath path) {
			IPath path2 = root.append(path);
			if( !changed.contains(path2.makeRelative()))
				changed.add(path2.makeRelative());
			return new IStatus[]{};
		}
	}
}
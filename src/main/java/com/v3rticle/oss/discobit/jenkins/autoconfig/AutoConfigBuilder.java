package com.v3rticle.oss.discobit.jenkins.autoconfig;


import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.Secret;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import com.v3rticle.oss.discobit.client.DiscobitClient;
import com.v3rticle.oss.discobit.client.DiscobitClientFactory;
import com.v3rticle.oss.discobit.client.DiscobitOperationException;


public class AutoConfigBuilder extends Builder {

    private final String discobitUrl;
    private final String discobitUser;
    private final Secret discobitPassword;
    private final String configurations;
    private final String cuuid;
    
    @DataBoundConstructor
    public AutoConfigBuilder(String name, String discobitUrl,
			String discobitUser, Secret discobitPassword,
			String configurations, String cuuid) {
		super();
		this.discobitUrl = discobitUrl;
		this.discobitUser = discobitUser;
		this.discobitPassword = discobitPassword;
		this.configurations = configurations;
		this.cuuid = cuuid;
		
		
		
	}

    /**
     * We'll use this from the <tt>config.jelly</tt>.
     */
    public String getDiscobitUrl() {
		return discobitUrl;
	}

	public String getDiscobitUser() {
		return discobitUser;
	}

	public Secret getDiscobitPassword() {
		return discobitPassword;
	}

	public String getConfigurations() {
		return configurations;
	}

	public String getCuuid() {
		return cuuid;
	}

	@Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
		System.out.println("performing discoBit autoConfig");
		
		
		listener.getLogger().println("");
		listener.getLogger().println("[    *** discoBit AutoConf ***    ]");
		listener.getLogger().println("[ Configuration Repository Plugin ]");
		listener.getLogger().println("[       http://discobit.com       ]");
		listener.getLogger().println("");
		listener.getLogger().println("[autoConfig] discobit url: " + getDescriptor().getDiscobitUrl());
		listener.getLogger().println("[autoConfig] discobit user: " + getDescriptor().getDiscobitUser());
		listener.getLogger().println("[autoConfig] configurations: " + configurations);
		listener.getLogger().println("[autoConfig] uuid: " + cuuid);
		
		URL serverUrl;
		try {
			serverUrl = new URL(getDescriptor().getDiscobitUrl());
		} catch (MalformedURLException e) {
			listener.error(e.getMessage());
			return false;
		}
		DiscobitClient discobit = DiscobitClientFactory.getClient(
				serverUrl, 
				getDescriptor().getDiscobitUser(), 
				getDescriptor().getDiscobitPassword().getPlainText());
		
		try {
			if (!discobit.testAuthentication()){
				listener.error("Error accessing discobit, check server settings.");
				return false;
			}
		} catch (DiscobitOperationException e) {
			listener.error("Error accessing discobit: " + e.getMessage());
			return false;
		}
		
		if (configurations != null){
			List<String> configurationsSeparated = Arrays.asList(StringUtils.split(configurations, ","));
			for (String singleConfig : configurationsSeparated) {
				singleConfig = singleConfig.trim();
				
				listener.getLogger().println("[autoConfig] validating file:" + singleConfig);
				File singleConfigFile = new File(build.getWorkspace().getRemote() + FileSystems.getDefault().getSeparator() + singleConfig);
				if (singleConfigFile.exists()){
					listener.getLogger().println("[autoConfig] pushing: " + cuuid + " <- " + singleConfig);
					boolean successPush = discobit.pushConfiguration(cuuid, singleConfigFile);
					listener.getLogger().println("[autoConfig] successful: " + successPush);
				} else {
					listener.getLogger().println("[autoConfig] skipping, cannot be located: " + cuuid + " <- " + singleConfig);
				}
			}
		}
		listener.getLogger().println("[autoConfig] finished.");
		
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    
    
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

    	private String discobitUrl;
        private String discobitUser;
        private Secret discobitPassword;

        public DescriptorImpl() {
            load();
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        @Override
        public String getDisplayName() {
        	return "Push configurations to the discoBit configuration repository";
        }

        /**
         * Applicable to any kind of project.
         */
        @Override
        public boolean isApplicable(Class type) {
            return true;
        }
        
        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            discobitUrl = formData.getString("discobitUrl");
            discobitUser = formData.getString("discobitUser");
            discobitPassword = Secret.fromString( formData.getString("discobitPassword") );
            
            save();
            return super.configure(req,formData);
        }
        
        
        public FormValidation doTestConnection(
    		@QueryParameter("discobitUrl") final String discobitUrl,
    		@QueryParameter("discobitUser") final String discobitUser,
    		@QueryParameter("discobitPassword") final String discobitPassword) throws IOException, ServletException {
    		try {
    			URL serverUrl = null;
    			try {
    				serverUrl = new URL(discobitUrl);
    			} catch (MalformedURLException e) {
    				return FormValidation.error("Failed. Mailformed URL");
    			}
    			DiscobitClient discobit = DiscobitClientFactory.getClient(
    					serverUrl, 
    					discobitUser,
    					discobitPassword);
    			
    			if( discobit.testAuthentication()) {
    				return FormValidation.ok("Success. Connection with discoBit Configuration Repository verified.");
    			}else{
    				return FormValidation.error("Failed. Connection with discoBit Configuration Repository not verified.");
    			}
    		} catch (Exception e) {
    			System.out.println("Exception " + e.getMessage() );
    			e.printStackTrace();
    			return FormValidation.error("Client error : " + e.getMessage());
    		}
    	}

		public String getDiscobitUrl() {
			return discobitUrl;
		}

		public String getDiscobitUser() {
			return discobitUser;
		}

		public Secret getDiscobitPassword() {
			return discobitPassword;
		}

    }
}


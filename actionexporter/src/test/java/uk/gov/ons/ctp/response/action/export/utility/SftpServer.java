package uk.gov.ons.ctp.response.action.export.utility;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.Collections;

import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;

public class SftpServer implements InitializingBean {

  @Override
  public void afterPropertiesSet() throws Exception {
    SshServer sftpServer = SshServer.setUpDefaultServer();
    sftpServer.setHost("localhost");
    sftpServer.setPort(8888);
    sftpServer.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
    sftpServer.setSubsystemFactories(Collections.<NamedFactory<Command>> singletonList(new SftpSubsystemFactory()));
    final String virtualDir = new FileSystemResource("").getFile().getAbsolutePath();
    sftpServer.setFileSystemFactory(new VirtualFileSystemFactory(Paths.get(virtualDir)));
    // Dummy always authenticate public key
    sftpServer.setPublickeyAuthenticator(new PublickeyAuthenticator() {
      @Override
      public boolean authenticate(String username, PublicKey key, ServerSession session) {
        return true;
      }
    });
    // Dummy always authenticate user
    sftpServer.setPasswordAuthenticator(new PasswordAuthenticator() {
      public boolean authenticate(String username, String password, ServerSession session) {
        return true;
      }
    });

    try {
      sftpServer.start();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

}

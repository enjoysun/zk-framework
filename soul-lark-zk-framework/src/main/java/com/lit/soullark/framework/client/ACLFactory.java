package com.lit.soullark.framework.client;

import com.lit.soullark.framework.enums.ACLType;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.imps.DefaultACLProvider;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author myou
 * @Date 2020/7/3  4:38 下午
 */
public class ACLFactory {

    private static class DigestACL implements ACLProvider {
        private String userAndPass;

        public DigestACL(String userAndPass) {
            this.userAndPass = userAndPass;
        }

        @Override
        public List<ACL> getDefaultAcl() {
            return getAcl();
        }

        @Override
        public List<ACL> getAclForPath(String s) {
            return getAcl();
        }

        private List<ACL> getAcl() {
            List<ACL> acl = new ArrayList<>();
            try {
                Id digest = new Id("digest", DigestACL.generateDigest(userAndPass));
                acl.add(new ACL(ZooDefs.Perms.READ, digest));
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(String.format("setACL error:%s", e.getMessage()));
            }
            return acl;
        }

        /**
         * digest:
         * userName:Base64(SHA-1(user:pass))
         */
        static public String generateDigest(String idPassword)
                throws NoSuchAlgorithmException {
            String[] parts = idPassword.split(":", 2);
            byte[] digest = MessageDigest.getInstance("SHA1").digest(
                    idPassword.getBytes());
            BASE64Encoder encoder = new BASE64Encoder();
            return parts[0] + ":" + encoder.encode(digest);
        }
    }

    /**
     * 暂且支持digest方式ACL
     *
     * @param userAndPath user:pass
     * @return
     */
    public static ACLProvider getAcl(ACLType aclType, String userAndPath) {
        if (aclType == ACLType.WORLD) {
            return new DefaultACLProvider();
        }
        if (aclType == ACLType.DIGEST) {
            return new DigestACL(userAndPath);
        }
        return new DefaultACLProvider();
    }
}

package datasource;

public class Contact {

    private String cid;
    private String alias;
    private String ipv4;
    private int tlsPort;

    public Contact(String cid, String alias, String ipv4, int tlsPort){
        this.cid = cid;
        this.alias = alias;
        this.ipv4 = ipv4;
        this.tlsPort = tlsPort;
    }

    public String getCid() {
        return cid;
    }

    public String getAlias() {
        return alias;
    }

    public String getIpv4() {
        return ipv4;
    }

    public int getTlsPort() {
        return tlsPort;
    }

    @Override
    public boolean equals(Object obj) {
        if( this == obj)
            return true;
        if(obj == null || getClass() != obj.getClass()){
            return false;
        }
        Contact contact = (Contact) obj;
        return cid.equals(contact.getCid()) &&
                alias.equals(contact.getAlias()) &&
                ipv4.equals(contact.getIpv4()) &&
                tlsPort == contact.getTlsPort();
    }
}

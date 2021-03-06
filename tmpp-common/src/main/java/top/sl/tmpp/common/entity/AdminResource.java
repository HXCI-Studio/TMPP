package top.sl.tmpp.common.entity;

import java.util.Date;

public class AdminResource {
    private String id;

    private String url;

    private String method;

    private Date gmtCreate;

    private Date gmtModified;

    public AdminResource(String id, String url, String method, Date gmtCreate, Date gmtModified) {
        this.id = id;
        this.url = url;
        this.method = method;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
    }

    public AdminResource() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method == null ? null : method.trim();
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    @Override
    public String toString() {
        return "AdminResource{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                '}';
    }
}
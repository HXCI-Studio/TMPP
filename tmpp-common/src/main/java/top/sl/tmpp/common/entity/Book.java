package top.sl.tmpp.common.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

public class Book {
    private String id;

    private String isbn;

    private String textBookName;

    private Boolean textBookCategory;

    private String press;

    private String author;

    private BigDecimal unitPrice;

    private Integer teacherBookNumber;

    private BigDecimal discount;

    private String awardInformation;
    @DateTimeFormat(pattern = "yyyy-MM")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "UTC")
    private Date publicationDate;

    private String subscriber;

    private String subscriberTel;

    private Boolean isBookPurchase;

    private String reason;

    private String loginUserId;

    private Boolean isBuyBook;

    private String executePlanId;

    private Integer status;

    private Date gmtModified;

    private Date gmtCreate;


    public Book(String id, Integer status) {
        this.id = id;
        this.status = status;
    }

    public Book(String id, String isbn, String textBookName, Boolean textBookCategory, String press, String author, BigDecimal unitPrice, Integer teacherBookNumber, BigDecimal discount, String awardInformation, Date publicationDate, String subscriber, String subscriberTel, Boolean isBookPurchase, String reason, String loginUserId, Boolean isBuyBook, String executePlanId, Integer status, Date gmtModified, Date gmtCreate) {
        this.id = id;
        this.isbn = isbn;
        this.textBookName = textBookName;
        this.textBookCategory = textBookCategory;
        this.press = press;
        this.author = author;
        this.unitPrice = unitPrice;
        this.teacherBookNumber = teacherBookNumber;
        this.discount = discount;
        this.awardInformation = awardInformation;
        this.publicationDate = publicationDate;
        this.subscriber = subscriber;
        this.subscriberTel = subscriberTel;
        this.isBookPurchase = isBookPurchase;
        this.reason = reason;
        this.loginUserId = loginUserId;
        this.isBuyBook = isBuyBook;
        this.executePlanId = executePlanId;
        this.status = status;
        this.gmtModified = gmtModified;
        this.gmtCreate = gmtCreate;
    }

    public Book() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn == null ? null : isbn.trim();
    }

    public String getTextBookName() {
        return textBookName;
    }

    public void setTextBookName(String textBookName) {
        this.textBookName = textBookName == null ? null : textBookName.trim();
    }

    public Boolean getTextBookCategory() {
        return textBookCategory;
    }

    public void setTextBookCategory(Boolean textBookCategory) {
        this.textBookCategory = textBookCategory;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press == null ? null : press.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author == null ? null : author.trim();
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getTeacherBookNumber() {
        return teacherBookNumber;
    }

    public void setTeacherBookNumber(Integer teacherBookNumber) {
        this.teacherBookNumber = teacherBookNumber;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getAwardInformation() {
        return awardInformation;
    }

    public void setAwardInformation(String awardInformation) {
        this.awardInformation = awardInformation == null ? null : awardInformation.trim();
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(String subscriber) {
        this.subscriber = subscriber == null ? null : subscriber.trim();
    }

    public String getSubscriberTel() {
        return subscriberTel;
    }

    public void setSubscriberTel(String subscriberTel) {
        this.subscriberTel = subscriberTel == null ? null : subscriberTel.trim();
    }

    public Boolean getIsBookPurchase() {
        return isBookPurchase;
    }

    public void setIsBookPurchase(Boolean isBookPurchase) {
        this.isBookPurchase = isBookPurchase;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(String loginUserId) {
        this.loginUserId = loginUserId == null ? null : loginUserId.trim();
    }

    public Boolean getIsBuyBook() {
        return isBuyBook;
    }

    public void setIsBuyBook(Boolean isBuyBook) {
        this.isBuyBook = isBuyBook;
    }

    public String getExecutePlanId() {
        return executePlanId;
    }

    public void setExecutePlanId(String executePlanId) {
        this.executePlanId = executePlanId == null ? null : executePlanId.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
}
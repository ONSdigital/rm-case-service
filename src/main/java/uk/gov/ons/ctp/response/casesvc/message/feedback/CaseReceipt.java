package uk.gov.ons.ctp.response.casesvc.message.feedback;

import com.kscs.util.jaxb.Buildable;
import com.kscs.util.jaxb.PropertyTree;
import com.kscs.util.jaxb.PropertyTreeUse;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Java class for CaseReceipt complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CaseReceipt"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="caseRef" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="caseId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="inboundChannel" type="{http://ons.gov.uk/ctp/response/casesvc/message/feedback}InboundChannel"/&gt;
 *         &lt;element name="responseDateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="partyId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "CaseReceipt",
    propOrder = {"caseRef", "caseId", "inboundChannel", "responseDateTime", "partyId"})
@XmlRootElement(name = "caseReceipt")
public class CaseReceipt {

  protected String caseRef;

  @XmlElement(required = true)
  protected String caseId;

  @XmlElement(required = true)
  @XmlSchemaType(name = "string")
  protected InboundChannel inboundChannel;

  @XmlElement(required = true)
  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar responseDateTime;

  protected String partyId;

  /** Default no-arg constructor */
  public CaseReceipt() {
    super();
  }

  /** Fully-initialising value constructor */
  public CaseReceipt(
      final String caseRef,
      final String caseId,
      final InboundChannel inboundChannel,
      final XMLGregorianCalendar responseDateTime,
      final String partyId) {
    this.caseRef = caseRef;
    this.caseId = caseId;
    this.inboundChannel = inboundChannel;
    this.responseDateTime = responseDateTime;
    this.partyId = partyId;
  }

  /**
   * Gets the value of the caseRef property.
   *
   * @return possible object is {@link String }
   */
  public String getCaseRef() {
    return caseRef;
  }

  /**
   * Sets the value of the caseRef property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCaseRef(String value) {
    this.caseRef = value;
  }

  /**
   * Gets the value of the caseId property.
   *
   * @return possible object is {@link String }
   */
  public String getCaseId() {
    return caseId;
  }

  /**
   * Sets the value of the caseId property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCaseId(String value) {
    this.caseId = value;
  }

  /**
   * Gets the value of the inboundChannel property.
   *
   * @return possible object is {@link InboundChannel }
   */
  public InboundChannel getInboundChannel() {
    return inboundChannel;
  }

  /**
   * Sets the value of the inboundChannel property.
   *
   * @param value allowed object is {@link InboundChannel }
   */
  public void setInboundChannel(InboundChannel value) {
    this.inboundChannel = value;
  }

  /**
   * Gets the value of the responseDateTime property.
   *
   * @return possible object is {@link XMLGregorianCalendar }
   */
  public XMLGregorianCalendar getResponseDateTime() {
    return responseDateTime;
  }

  /**
   * Sets the value of the responseDateTime property.
   *
   * @param value allowed object is {@link XMLGregorianCalendar }
   */
  public void setResponseDateTime(XMLGregorianCalendar value) {
    this.responseDateTime = value;
  }

  /**
   * Gets the value of the partyId property.
   *
   * @return possible object is {@link String }
   */
  public String getPartyId() {
    return partyId;
  }

  /**
   * Sets the value of the partyId property.
   *
   * @param value allowed object is {@link String }
   */
  public void setPartyId(String value) {
    this.partyId = value;
  }

  /**
   * Copies all state of this object to a builder. This method is used by the {@link #copyOf} method
   * and should not be called directly by client code.
   *
   * @param _other A builder instance to which the state of this object will be copied.
   */
  public <_B> void copyTo(final Builder<_B> _other) {
    _other.caseRef = this.caseRef;
    _other.caseId = this.caseId;
    _other.inboundChannel = this.inboundChannel;
    _other.responseDateTime =
        ((this.responseDateTime == null)
            ? null
            : ((XMLGregorianCalendar) this.responseDateTime.clone()));
    _other.partyId = this.partyId;
  }

  public <_B> Builder<_B> newCopyBuilder(final _B _parentBuilder) {
    return new Builder<_B>(_parentBuilder, this, true);
  }

  public Builder<Void> newCopyBuilder() {
    return newCopyBuilder(null);
  }

  public static Builder<Void> builder() {
    return new Builder<Void>(null, null, false);
  }

  public static <_B> Builder<_B> copyOf(final CaseReceipt _other) {
    final Builder<_B> _newBuilder = new Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder);
    return _newBuilder;
  }

  /**
   * Copies all state of this object to a builder. This method is used by the {@link #copyOf} method
   * and should not be called directly by client code.
   *
   * @param _other A builder instance to which the state of this object will be copied.
   */
  public <_B> void copyTo(
      final Builder<_B> _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final PropertyTree caseRefPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("caseRef"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (caseRefPropertyTree != null)
        : ((caseRefPropertyTree == null) || (!caseRefPropertyTree.isLeaf())))) {
      _other.caseRef = this.caseRef;
    }
    final PropertyTree caseIdPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("caseId"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (caseIdPropertyTree != null)
        : ((caseIdPropertyTree == null) || (!caseIdPropertyTree.isLeaf())))) {
      _other.caseId = this.caseId;
    }
    final PropertyTree inboundChannelPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("inboundChannel"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (inboundChannelPropertyTree != null)
        : ((inboundChannelPropertyTree == null) || (!inboundChannelPropertyTree.isLeaf())))) {
      _other.inboundChannel = this.inboundChannel;
    }
    final PropertyTree responseDateTimePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("responseDateTime"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (responseDateTimePropertyTree != null)
        : ((responseDateTimePropertyTree == null) || (!responseDateTimePropertyTree.isLeaf())))) {
      _other.responseDateTime =
          ((this.responseDateTime == null)
              ? null
              : ((XMLGregorianCalendar) this.responseDateTime.clone()));
    }
    final PropertyTree partyIdPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("partyId"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (partyIdPropertyTree != null)
        : ((partyIdPropertyTree == null) || (!partyIdPropertyTree.isLeaf())))) {
      _other.partyId = this.partyId;
    }
  }

  public <_B> Builder<_B> newCopyBuilder(
      final _B _parentBuilder,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    return new Builder<_B>(_parentBuilder, this, true, _propertyTree, _propertyTreeUse);
  }

  public Builder<Void> newCopyBuilder(
      final PropertyTree _propertyTree, final PropertyTreeUse _propertyTreeUse) {
    return newCopyBuilder(null, _propertyTree, _propertyTreeUse);
  }

  public static <_B> Builder<_B> copyOf(
      final CaseReceipt _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final Builder<_B> _newBuilder = new Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder, _propertyTree, _propertyTreeUse);
    return _newBuilder;
  }

  public static Builder<Void> copyExcept(
      final CaseReceipt _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.EXCLUDE);
  }

  public static Builder<Void> copyOnly(final CaseReceipt _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.INCLUDE);
  }

  public static class Builder<_B> implements Buildable {

    protected final _B _parentBuilder;
    protected final CaseReceipt _storedValue;
    private String caseRef;
    private String caseId;
    private InboundChannel inboundChannel;
    private XMLGregorianCalendar responseDateTime;
    private String partyId;

    public Builder(final _B _parentBuilder, final CaseReceipt _other, final boolean _copy) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          this.caseRef = _other.caseRef;
          this.caseId = _other.caseId;
          this.inboundChannel = _other.inboundChannel;
          this.responseDateTime =
              ((_other.responseDateTime == null)
                  ? null
                  : ((XMLGregorianCalendar) _other.responseDateTime.clone()));
          this.partyId = _other.partyId;
        } else {
          _storedValue = _other;
        }
      } else {
        _storedValue = null;
      }
    }

    public Builder(
        final _B _parentBuilder,
        final CaseReceipt _other,
        final boolean _copy,
        final PropertyTree _propertyTree,
        final PropertyTreeUse _propertyTreeUse) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          final PropertyTree caseRefPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("caseRef"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (caseRefPropertyTree != null)
              : ((caseRefPropertyTree == null) || (!caseRefPropertyTree.isLeaf())))) {
            this.caseRef = _other.caseRef;
          }
          final PropertyTree caseIdPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("caseId"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (caseIdPropertyTree != null)
              : ((caseIdPropertyTree == null) || (!caseIdPropertyTree.isLeaf())))) {
            this.caseId = _other.caseId;
          }
          final PropertyTree inboundChannelPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("inboundChannel"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (inboundChannelPropertyTree != null)
              : ((inboundChannelPropertyTree == null) || (!inboundChannelPropertyTree.isLeaf())))) {
            this.inboundChannel = _other.inboundChannel;
          }
          final PropertyTree responseDateTimePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("responseDateTime"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (responseDateTimePropertyTree != null)
              : ((responseDateTimePropertyTree == null)
                  || (!responseDateTimePropertyTree.isLeaf())))) {
            this.responseDateTime =
                ((_other.responseDateTime == null)
                    ? null
                    : ((XMLGregorianCalendar) _other.responseDateTime.clone()));
          }
          final PropertyTree partyIdPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("partyId"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (partyIdPropertyTree != null)
              : ((partyIdPropertyTree == null) || (!partyIdPropertyTree.isLeaf())))) {
            this.partyId = _other.partyId;
          }
        } else {
          _storedValue = _other;
        }
      } else {
        _storedValue = null;
      }
    }

    public _B end() {
      return this._parentBuilder;
    }

    protected <_P extends CaseReceipt> _P init(final _P _product) {
      _product.caseRef = this.caseRef;
      _product.caseId = this.caseId;
      _product.inboundChannel = this.inboundChannel;
      _product.responseDateTime = this.responseDateTime;
      _product.partyId = this.partyId;
      return _product;
    }

    /**
     * Sets the new value of "caseRef" (any previous value will be replaced)
     *
     * @param caseRef New value of the "caseRef" property.
     */
    public Builder<_B> withCaseRef(final String caseRef) {
      this.caseRef = caseRef;
      return this;
    }

    /**
     * Sets the new value of "caseId" (any previous value will be replaced)
     *
     * @param caseId New value of the "caseId" property.
     */
    public Builder<_B> withCaseId(final String caseId) {
      this.caseId = caseId;
      return this;
    }

    /**
     * Sets the new value of "inboundChannel" (any previous value will be replaced)
     *
     * @param inboundChannel New value of the "inboundChannel" property.
     */
    public Builder<_B> withInboundChannel(final InboundChannel inboundChannel) {
      this.inboundChannel = inboundChannel;
      return this;
    }

    /**
     * Sets the new value of "responseDateTime" (any previous value will be replaced)
     *
     * @param responseDateTime New value of the "responseDateTime" property.
     */
    public Builder<_B> withResponseDateTime(final XMLGregorianCalendar responseDateTime) {
      this.responseDateTime = responseDateTime;
      return this;
    }

    /**
     * Sets the new value of "partyId" (any previous value will be replaced)
     *
     * @param partyId New value of the "partyId" property.
     */
    public Builder<_B> withPartyId(final String partyId) {
      this.partyId = partyId;
      return this;
    }

    @Override
    public CaseReceipt build() {
      if (_storedValue == null) {
        return this.init(new CaseReceipt());
      } else {
        return ((CaseReceipt) _storedValue);
      }
    }
  }

  public static class Select extends Selector<Select, Void> {

    Select() {
      super(null, null, null);
    }

    public static Select _root() {
      return new Select();
    }
  }

  public static class Selector<TRoot extends com.kscs.util.jaxb.Selector<TRoot, ?>, TParent>
      extends com.kscs.util.jaxb.Selector<TRoot, TParent> {

    private com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> caseRef = null;
    private com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> caseId = null;
    private com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> inboundChannel = null;
    private com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> responseDateTime = null;
    private com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> partyId = null;

    public Selector(final TRoot root, final TParent parent, final String propertyName) {
      super(root, parent, propertyName);
    }

    @Override
    public Map<String, PropertyTree> buildChildren() {
      final Map<String, PropertyTree> products = new HashMap<String, PropertyTree>();
      products.putAll(super.buildChildren());
      if (this.caseRef != null) {
        products.put("caseRef", this.caseRef.init());
      }
      if (this.caseId != null) {
        products.put("caseId", this.caseId.init());
      }
      if (this.inboundChannel != null) {
        products.put("inboundChannel", this.inboundChannel.init());
      }
      if (this.responseDateTime != null) {
        products.put("responseDateTime", this.responseDateTime.init());
      }
      if (this.partyId != null) {
        products.put("partyId", this.partyId.init());
      }
      return products;
    }

    public com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> caseRef() {
      return ((this.caseRef == null)
          ? this.caseRef =
              new com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>>(
                  this._root, this, "caseRef")
          : this.caseRef);
    }

    public com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> caseId() {
      return ((this.caseId == null)
          ? this.caseId =
              new com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>>(
                  this._root, this, "caseId")
          : this.caseId);
    }

    public com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> inboundChannel() {
      return ((this.inboundChannel == null)
          ? this.inboundChannel =
              new com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>>(
                  this._root, this, "inboundChannel")
          : this.inboundChannel);
    }

    public com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> responseDateTime() {
      return ((this.responseDateTime == null)
          ? this.responseDateTime =
              new com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>>(
                  this._root, this, "responseDateTime")
          : this.responseDateTime);
    }

    public com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> partyId() {
      return ((this.partyId == null)
          ? this.partyId =
              new com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>>(
                  this._root, this, "partyId")
          : this.partyId);
    }
  }
}

package uk.gov.ons.ctp.response.casesvc.message.notification;

import com.kscs.util.jaxb.Buildable;
import com.kscs.util.jaxb.PropertyTree;
import com.kscs.util.jaxb.PropertyTreeUse;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.*;

/**
 * Java class for CaseNotification complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CaseNotification"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;all&gt;
 *         &lt;element name="sampleUnitId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="caseId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="exerciseId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="partyId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="sampleUnitType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="notificationType" type="{http://ons.gov.uk/ctp/response/casesvc/message/notification}NotificationType"/&gt;
 *       &lt;/all&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "CaseNotification",
        propOrder = {})
@XmlRootElement(name = "caseNotification")
public class CaseNotification {

  protected String sampleUnitId;

  @XmlElement(required = true)
  protected String caseId;

  protected boolean activeEnrolment;

  @XmlElement(required = true)
  protected String exerciseId;

  protected String partyId;

  protected String sampleUnitType;

  @XmlElement(required = true)
  @XmlSchemaType(name = "string")
  protected NotificationType notificationType;

  protected String sampleUnitRef;

  protected String status;

  protected String iac;

  /** Default no-arg constructor */
  public CaseNotification() {
    super();
  }

  /** Fully-initialising value constructor */
  public CaseNotification(
          final String sampleUnitId,
          final String caseId,
          final boolean activeEnrolment,
          final String exerciseId,
          final String partyId,
          final String sampleUnitType,
          final NotificationType notificationType,
          final String sampleUnitRef,
          final String status,
          final String iac) {
    this.sampleUnitId = sampleUnitId;
    this.caseId = caseId;
    this.exerciseId = exerciseId;
    this.partyId = partyId;
    this.sampleUnitType = sampleUnitType;
    this.notificationType = notificationType;
    this.activeEnrolment = activeEnrolment;
    this.sampleUnitRef = sampleUnitRef;
    this.status = status;
    this.iac = iac;
  }

  /**
   * Gets the value of the sampleUnitId property.
   *
   * @return possible object is {@link String }
   */
  public String getSampleUnitId() {
    return sampleUnitId;
  }

  /**
   * Sets the value of the sampleUnitId property.
   *
   * @param value allowed object is {@link String }
   */
  public void setSampleUnitId(String value) {
    this.sampleUnitId = value;
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

  public boolean isActiveEnrolment() {
    return activeEnrolment;
  }

  public void setActiveEnrolment(boolean activeEnrolment) {
    this.activeEnrolment = activeEnrolment;
  }

  /**
   * Gets the value of the exerciseId property.
   *
   * @return possible object is {@link String }
   */
  public String getExerciseId() {
    return exerciseId;
  }

  /**
   * Sets the value of the exerciseId property.
   *
   * @param value allowed object is {@link String }
   */
  public void setExerciseId(String value) {
    this.exerciseId = value;
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
   * Gets the value of the sampleUnitType property.
   *
   * @return possible object is {@link String }
   */
  public String getSampleUnitType() {
    return sampleUnitType;
  }

  /**
   * Sets the value of the sampleUnitType property.
   *
   * @param value allowed object is {@link String }
   */
  public void setSampleUnitType(String value) {
    this.sampleUnitType = value;
  }

  /**
   * Gets the value of the notificationType property.
   *
   * @return possible object is {@link NotificationType }
   */
  public NotificationType getNotificationType() {
    return notificationType;
  }

  /**
   * Sets the value of the notificationType property.
   *
   * @param value allowed object is {@link NotificationType }
   */
  public void setNotificationType(NotificationType value) {
    this.notificationType = value;
  }

  public String getSampleUnitRef() {
    return sampleUnitRef;
  }

  public void setSampleUnitRef(String sampleUnitRef) {
    this.sampleUnitRef = sampleUnitRef;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getIac() {
    return iac;
  }

  public void setIac(String iac) {
    this.iac = iac;
  }

  /**
   * Copies all state of this object to a builder. This method is used by the {@link #copyOf} method
   * and should not be called directly by client code.
   *
   * @param _other A builder instance to which the state of this object will be copied.
   */
  public <_B> void copyTo(final CaseNotification.Builder<_B> _other) {
    _other.sampleUnitId = this.sampleUnitId;
    _other.caseId = this.caseId;
    _other.activeEnrolment = this.activeEnrolment;
    _other.exerciseId = this.exerciseId;
    _other.partyId = this.partyId;
    _other.sampleUnitType = this.sampleUnitType;
    _other.notificationType = this.notificationType;
    _other.sampleUnitRef = this.sampleUnitRef;
    _other.status = this.status;
    _other.iac = this.iac;
  }

  public <_B> CaseNotification.Builder<_B> newCopyBuilder(final _B _parentBuilder) {
    return new CaseNotification.Builder<_B>(_parentBuilder, this, true);
  }

  public CaseNotification.Builder<Void> newCopyBuilder() {
    return newCopyBuilder(null);
  }

  public static CaseNotification.Builder<Void> builder() {
    return new CaseNotification.Builder<Void>(null, null, false);
  }

  public static <_B> CaseNotification.Builder<_B> copyOf(final CaseNotification _other) {
    final CaseNotification.Builder<_B> _newBuilder =
            new CaseNotification.Builder<_B>(null, null, false);
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
          final CaseNotification.Builder<_B> _other,
          final PropertyTree _propertyTree,
          final PropertyTreeUse _propertyTreeUse) {
    final PropertyTree sampleUnitIdPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("sampleUnitId"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (sampleUnitIdPropertyTree != null)
            : ((sampleUnitIdPropertyTree == null) || (!sampleUnitIdPropertyTree.isLeaf())))) {
      _other.sampleUnitId = this.sampleUnitId;
    }
    final PropertyTree caseIdPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("caseId"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (caseIdPropertyTree != null)
            : ((caseIdPropertyTree == null) || (!caseIdPropertyTree.isLeaf())))) {
      _other.caseId = this.caseId;
    }
    final PropertyTree activeEnrolmentPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("activeEnrolment"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (activeEnrolmentPropertyTree != null)
            : ((activeEnrolmentPropertyTree == null) || (!activeEnrolmentPropertyTree.isLeaf())))) {
      _other.activeEnrolment = this.activeEnrolment;
    }
    final PropertyTree exerciseIdPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("exerciseId"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (exerciseIdPropertyTree != null)
            : ((exerciseIdPropertyTree == null) || (!exerciseIdPropertyTree.isLeaf())))) {
      _other.exerciseId = this.exerciseId;
    }
    final PropertyTree partyIdPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("partyId"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (partyIdPropertyTree != null)
            : ((partyIdPropertyTree == null) || (!partyIdPropertyTree.isLeaf())))) {
      _other.partyId = this.partyId;
    }
    final PropertyTree sampleUnitTypePropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("sampleUnitType"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (sampleUnitTypePropertyTree != null)
            : ((sampleUnitTypePropertyTree == null) || (!sampleUnitTypePropertyTree.isLeaf())))) {
      _other.sampleUnitType = this.sampleUnitType;
    }
    final PropertyTree notificationTypePropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("notificationType"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (notificationTypePropertyTree != null)
            : ((notificationTypePropertyTree == null) || (!notificationTypePropertyTree.isLeaf())))) {
      _other.notificationType = this.notificationType;
    }
    final PropertyTree sampleUnitRefPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("sampleUnitRef"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (sampleUnitRefPropertyTree != null)
            : ((sampleUnitRefPropertyTree == null) || (!sampleUnitRefPropertyTree.isLeaf())))) {
      _other.sampleUnitRef = this.sampleUnitRef;
    }
    final PropertyTree statusPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("status"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (statusPropertyTree != null)
            : ((statusPropertyTree == null) || (!statusPropertyTree.isLeaf())))) {
      _other.status = this.status;
    }
    final PropertyTree iacPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("iac"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (iacPropertyTree != null)
            : ((iacPropertyTree == null) || (!iacPropertyTree.isLeaf())))) {
      _other.iac = this.iac;
    }
  }

  public <_B> CaseNotification.Builder<_B> newCopyBuilder(
          final _B _parentBuilder,
          final PropertyTree _propertyTree,
          final PropertyTreeUse _propertyTreeUse) {
    return new CaseNotification.Builder<_B>(
            _parentBuilder, this, true, _propertyTree, _propertyTreeUse);
  }

  public CaseNotification.Builder<Void> newCopyBuilder(
          final PropertyTree _propertyTree, final PropertyTreeUse _propertyTreeUse) {
    return newCopyBuilder(null, _propertyTree, _propertyTreeUse);
  }

  public static <_B> CaseNotification.Builder<_B> copyOf(
          final CaseNotification _other,
          final PropertyTree _propertyTree,
          final PropertyTreeUse _propertyTreeUse) {
    final CaseNotification.Builder<_B> _newBuilder =
            new CaseNotification.Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder, _propertyTree, _propertyTreeUse);
    return _newBuilder;
  }

  public static CaseNotification.Builder<Void> copyExcept(
          final CaseNotification _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.EXCLUDE);
  }

  public static CaseNotification.Builder<Void> copyOnly(
          final CaseNotification _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.INCLUDE);
  }

  public static class Builder<_B> implements Buildable {

    protected final _B _parentBuilder;
    protected final CaseNotification _storedValue;
    private String sampleUnitId;
    private String caseId;
    private boolean activeEnrolment;
    private String exerciseId;
    private String partyId;
    private String sampleUnitType;
    private NotificationType notificationType;
    private String sampleUnitRef;
    private String status;
    private String iac;

    public Builder(final _B _parentBuilder, final CaseNotification _other, final boolean _copy) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          this.sampleUnitId = _other.sampleUnitId;
          this.caseId = _other.caseId;
          this.activeEnrolment = _other.activeEnrolment;
          this.exerciseId = _other.exerciseId;
          this.partyId = _other.partyId;
          this.sampleUnitType = _other.sampleUnitType;
          this.notificationType = _other.notificationType;
          this.sampleUnitRef = _other.sampleUnitRef;
          this.status = _other.status;
          this.iac = _other.iac;
        } else {
          _storedValue = _other;
        }
      } else {
        _storedValue = null;
      }
    }

    public Builder(
            final _B _parentBuilder,
            final CaseNotification _other,
            final boolean _copy,
            final PropertyTree _propertyTree,
            final PropertyTreeUse _propertyTreeUse) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          final PropertyTree sampleUnitIdPropertyTree =
                  ((_propertyTree == null) ? null : _propertyTree.get("sampleUnitId"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
                  ? (sampleUnitIdPropertyTree != null)
                  : ((sampleUnitIdPropertyTree == null) || (!sampleUnitIdPropertyTree.isLeaf())))) {
            this.sampleUnitId = _other.sampleUnitId;
          }
          final PropertyTree caseIdPropertyTree =
                  ((_propertyTree == null) ? null : _propertyTree.get("caseId"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
                  ? (caseIdPropertyTree != null)
                  : ((caseIdPropertyTree == null) || (!caseIdPropertyTree.isLeaf())))) {
            this.caseId = _other.caseId;
          }
          final PropertyTree activeEnrolmentPropertyTree =
                  ((_propertyTree == null) ? null : _propertyTree.get("activeEnrolment"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
                  ? (activeEnrolmentPropertyTree != null)
                  : ((activeEnrolmentPropertyTree == null) || (!activeEnrolmentPropertyTree.isLeaf())))) {
            this.activeEnrolment = _other.activeEnrolment;
          }
          final PropertyTree exerciseIdPropertyTree =
                  ((_propertyTree == null) ? null : _propertyTree.get("exerciseId"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
                  ? (exerciseIdPropertyTree != null)
                  : ((exerciseIdPropertyTree == null) || (!exerciseIdPropertyTree.isLeaf())))) {
            this.exerciseId = _other.exerciseId;
          }
          final PropertyTree partyIdPropertyTree =
                  ((_propertyTree == null) ? null : _propertyTree.get("partyId"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
                  ? (partyIdPropertyTree != null)
                  : ((partyIdPropertyTree == null) || (!partyIdPropertyTree.isLeaf())))) {
            this.partyId = _other.partyId;
          }
          final PropertyTree sampleUnitTypePropertyTree =
                  ((_propertyTree == null) ? null : _propertyTree.get("sampleUnitType"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
                  ? (sampleUnitTypePropertyTree != null)
                  : ((sampleUnitTypePropertyTree == null) || (!sampleUnitTypePropertyTree.isLeaf())))) {
            this.sampleUnitType = _other.sampleUnitType;
          }
          final PropertyTree notificationTypePropertyTree =
                  ((_propertyTree == null) ? null : _propertyTree.get("notificationType"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
                  ? (notificationTypePropertyTree != null)
                  : ((notificationTypePropertyTree == null)
                  || (!notificationTypePropertyTree.isLeaf())))) {
            this.notificationType = _other.notificationType;
          }
          final PropertyTree sampleUnitRefPropertyTree =
                  ((_propertyTree == null) ? null : _propertyTree.get("sampleUnitRef"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
                  ? (sampleUnitRefPropertyTree != null)
                  : ((sampleUnitRefPropertyTree == null)
                  || (!sampleUnitRefPropertyTree.isLeaf())))) {
            this.sampleUnitRef = _other.sampleUnitRef;
          }
          final PropertyTree statusPropertyTree =
                  ((_propertyTree == null) ? null : _propertyTree.get("status"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
                  ? (statusPropertyTree != null)
                  : ((statusPropertyTree == null)
                  || (!statusPropertyTree.isLeaf())))) {
            this.status = _other.status;
          }
          final PropertyTree iacPropertyTree =
                  ((_propertyTree == null) ? null : _propertyTree.get("iac"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
                  ? (iacPropertyTree != null)
                  : ((iacPropertyTree == null)
                  || (!iacPropertyTree.isLeaf())))) {
            this.iac = _other.iac;
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

    protected <_P extends CaseNotification> _P init(final _P _product) {
      _product.sampleUnitId = this.sampleUnitId;
      _product.caseId = this.caseId;
      _product.activeEnrolment = this.activeEnrolment;
      _product.exerciseId = this.exerciseId;
      _product.partyId = this.partyId;
      _product.sampleUnitType = this.sampleUnitType;
      _product.notificationType = this.notificationType;
      _product.sampleUnitRef = this.sampleUnitRef;
      _product.status = this.status;
      _product.iac = this.iac;
      return _product;
    }

    /**
     * Sets the new value of "sampleUnitId" (any previous value will be replaced)
     *
     * @param sampleUnitId New value of the "sampleUnitId" property.
     */
    public CaseNotification.Builder<_B> withSampleUnitId(final String sampleUnitId) {
      this.sampleUnitId = sampleUnitId;
      return this;
    }

    /**
     * Sets the new value of "caseId" (any previous value will be replaced)
     *
     * @param caseId New value of the "caseId" property.
     */
    public CaseNotification.Builder<_B> withCaseId(final String caseId) {
      this.caseId = caseId;
      return this;
    }

    public CaseNotification.Builder<_B> withActiveEnrolment(final boolean activeEnrolment) {
      this.activeEnrolment = activeEnrolment;
      return this;
    }

    /**
     * Sets the new value of "exerciseId" (any previous value will be replaced)
     *
     * @param exerciseId New value of the "exerciseId" property.
     */
    public CaseNotification.Builder<_B> withExerciseId(final String exerciseId) {
      this.exerciseId = exerciseId;
      return this;
    }

    /**
     * Sets the new value of "partyId" (any previous value will be replaced)
     *
     * @param partyId New value of the "partyId" property.
     */
    public CaseNotification.Builder<_B> withPartyId(final String partyId) {
      this.partyId = partyId;
      return this;
    }

    /**
     * Sets the new value of "sampleUnitType" (any previous value will be replaced)
     *
     * @param sampleUnitType New value of the "sampleUnitType" property.
     */
    public CaseNotification.Builder<_B> withSampleUnitType(final String sampleUnitType) {
      this.sampleUnitType = sampleUnitType;
      return this;
    }

    /**
     * Sets the new value of "notificationType" (any previous value will be replaced)
     *
     * @param notificationType New value of the "notificationType" property.
     */
    public CaseNotification.Builder<_B> withNotificationType(
            final NotificationType notificationType) {
      this.notificationType = notificationType;
      return this;
    }

    public CaseNotification.Builder<_B> withSampleUnitRef(
            final String sampleUnitRef) {
      this.sampleUnitRef = sampleUnitRef;
      return this;
    }

    public CaseNotification.Builder<_B> withStatus(
            final String status) {
      this.status = status;
      return this;
    }

    public CaseNotification.Builder<_B> withIac(
            final String iac) {
      this.iac = iac;
      return this;
    }

    @Override
    public CaseNotification build() {
      if (_storedValue == null) {
        return this.init(new CaseNotification());
      } else {
        return ((CaseNotification) _storedValue);
      }
    }
  }

  public static class Select extends CaseNotification.Selector<CaseNotification.Select, Void> {

    Select() {
      super(null, null, null);
    }

    public static CaseNotification.Select _root() {
      return new CaseNotification.Select();
    }
  }

  public static class Selector<TRoot extends com.kscs.util.jaxb.Selector<TRoot, ?>, TParent>
          extends com.kscs.util.jaxb.Selector<TRoot, TParent> {

    private com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
            sampleUnitId = null;
    private com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>> caseId =
            null;
    private com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
            activeEnrolment = null;
    private com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
            exerciseId = null;
    private com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>> partyId =
            null;
    private com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
            sampleUnitType = null;
    private com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
            notificationType = null;
    private com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
            sampleUnitRef = null;
    private com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
            status = null;
    private com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
            iac = null;

    public Selector(final TRoot root, final TParent parent, final String propertyName) {
      super(root, parent, propertyName);
    }

    @Override
    public Map<String, PropertyTree> buildChildren() {
      final Map<String, PropertyTree> products = new HashMap<String, PropertyTree>();
      products.putAll(super.buildChildren());
      if (this.sampleUnitId != null) {
        products.put("sampleUnitId", this.sampleUnitId.init());
      }
      if (this.caseId != null) {
        products.put("caseId", this.caseId.init());
      }
      if (this.activeEnrolment != null) {
        products.put("activeEnrolment", this.activeEnrolment.init());
      }
      if (this.exerciseId != null) {
        products.put("exerciseId", this.exerciseId.init());
      }
      if (this.partyId != null) {
        products.put("partyId", this.partyId.init());
      }
      if (this.sampleUnitType != null) {
        products.put("sampleUnitType", this.sampleUnitType.init());
      }
      if (this.notificationType != null) {
        products.put("notificationType", this.notificationType.init());
      }
      if (this.sampleUnitRef != null) {
        products.put("sampleUnitRef", this.sampleUnitRef.init());
      }
      if (this.status != null) {
        products.put("status", this.status.init());
      }
      if (this.iac != null) {
        products.put("iac", this.iac.init());
      }
      return products;
    }

    public com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
    sampleUnitId() {
      return ((this.sampleUnitId == null)
              ? this.sampleUnitId =
              new com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>(
                      this._root, this, "sampleUnitId")
              : this.sampleUnitId);
    }

    public com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>> caseId() {
      return ((this.caseId == null)
              ? this.caseId =
              new com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>(
                      this._root, this, "caseId")
              : this.caseId);
    }

    public com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
    actionEnrolment() {
      return ((this.activeEnrolment == null)
              ? this.activeEnrolment =
              new com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>(
                      this._root, this, "activeEnrolment")
              : this.activeEnrolment);
    }

    public com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
    exerciseId() {
      return ((this.exerciseId == null)
              ? this.exerciseId =
              new com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>(
                      this._root, this, "exerciseId")
              : this.exerciseId);
    }

    public com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>> partyId() {
      return ((this.partyId == null)
              ? this.partyId =
              new com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>(
                      this._root, this, "partyId")
              : this.partyId);
    }

    public com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
    sampleUnitType() {
      return ((this.sampleUnitType == null)
              ? this.sampleUnitType =
              new com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>(
                      this._root, this, "sampleUnitType")
              : this.sampleUnitType);
    }

    public com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
    notificationType() {
      return ((this.notificationType == null)
              ? this.notificationType =
              new com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>(
                      this._root, this, "notificationType")
              : this.notificationType);
    }

    public com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
    sampleUnitRef() {
      return ((this.sampleUnitRef == null)
              ? this.sampleUnitRef =
              new com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>(
                      this._root, this, "sampleUnitRef")
              : this.sampleUnitRef);
    }

    public com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
    status() {
      return ((this.status == null)
              ? this.status =
              new com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>(
                      this._root, this, "status")
              : this.status);
    }

    public com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>
    iac() {
      return ((this.iac == null)
              ? this.iac =
              new com.kscs.util.jaxb.Selector<TRoot, CaseNotification.Selector<TRoot, TParent>>(
                      this._root, this, "iac")
              : this.iac);
    }
  }
}
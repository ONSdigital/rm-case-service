package uk.gov.ons.ctp.response.casesvc.definition;

import com.kscs.util.jaxb.Buildable;
import com.kscs.util.jaxb.PropertyTree;
import com.kscs.util.jaxb.PropertyTreeUse;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.*;

/**
 * Java class for CaseCreation complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="CaseCreation"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="partyId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="collectionExerciseId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="collectionInstrumentId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="sampleUnitRef" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="sampleUnitType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="actionPlanId" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="activeEnrolment" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "CaseCreation",
    propOrder = {
      "partyId",
      "collectionExerciseId",
      "collectionInstrumentId",
      "sampleUnitRef",
      "sampleUnitType",
      "actionPlanId",
      "activeEnrolment"
    })
@XmlRootElement(name = "caseCreation")
public class CaseCreation {

  @XmlElement(required = true)
  protected String partyId;

  @XmlElement(required = true)
  protected String collectionExerciseId;

  @XmlElement(required = true)
  protected String collectionInstrumentId;

  @XmlElement(required = true)
  protected String sampleUnitRef;

  @XmlElement(required = true)
  protected String sampleUnitType;
  @XmlElement(nillable = true)
  protected String actionPlanId;
  protected boolean activeEnrolment;

  /** Default no-arg constructor */
  public CaseCreation() {
    super();
  }

  /** Fully-initialising value constructor */
  public CaseCreation(
      final String partyId,
      final String collectionExerciseId,
      final String collectionInstrumentId,
      final String sampleUnitRef,
      final String sampleUnitType,
      final String actionPlanId,
      final boolean activeEnrolment) {
    this.partyId = partyId;
    this.collectionExerciseId = collectionExerciseId;
    this.collectionInstrumentId = collectionInstrumentId;
    this.sampleUnitRef = sampleUnitRef;
    this.sampleUnitType = sampleUnitType;
    this.actionPlanId = actionPlanId;
    this.activeEnrolment = activeEnrolment;
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
   * Gets the value of the collectionExerciseId property.
   *
   * @return possible object is {@link String }
   */
  public String getCollectionExerciseId() {
    return collectionExerciseId;
  }

  /**
   * Sets the value of the collectionExerciseId property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCollectionExerciseId(String value) {
    this.collectionExerciseId = value;
  }

  /**
   * Gets the value of the collectionInstrumentId property.
   *
   * @return possible object is {@link String }
   */
  public String getCollectionInstrumentId() {
    return collectionInstrumentId;
  }

  /**
   * Sets the value of the collectionInstrumentId property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCollectionInstrumentId(String value) {
    this.collectionInstrumentId = value;
  }

  /**
   * Gets the value of the sampleUnitRef property.
   *
   * @return possible object is {@link String }
   */
  public String getSampleUnitRef() {
    return sampleUnitRef;
  }

  /**
   * Sets the value of the sampleUnitRef property.
   *
   * @param value allowed object is {@link String }
   */
  public void setSampleUnitRef(String value) {
    this.sampleUnitRef = value;
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
   * Gets the value of the actionPlanId property.
   *
   * @return possible object is {@link String }
   */
  public String getActionPlanId() {
    return actionPlanId;
  }

  /**
   * Sets the value of the actionPlanId property.
   *
   * @param value allowed object is {@link String }
   */
  public void setActionPlanId(String value) {
    if(actionPlanId != null) {
      this.actionPlanId = value;
    }
  }

  public boolean isActiveEnrolment() {
    return activeEnrolment;
  }

  public void setActiveEnrolment(boolean activeEnrolment) {
    this.activeEnrolment = activeEnrolment;
  }

  /**
   * Copies all state of this object to a builder. This method is used by the {@link #copyOf} method
   * and should not be called directly by client code.
   *
   * @param _other A builder instance to which the state of this object will be copied.
   */
  public <_B> void copyTo(final Builder<_B> _other) {
    _other.partyId = this.partyId;
    _other.collectionExerciseId = this.collectionExerciseId;
    _other.collectionInstrumentId = this.collectionInstrumentId;
    _other.sampleUnitRef = this.sampleUnitRef;
    _other.sampleUnitType = this.sampleUnitType;
    _other.actionPlanId = this.actionPlanId;
    _other.activeEnrolment = this.activeEnrolment;
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

  public static <_B> Builder<_B> copyOf(final CaseCreation _other) {
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
    final PropertyTree partyIdPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("partyId"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (partyIdPropertyTree != null)
        : ((partyIdPropertyTree == null) || (!partyIdPropertyTree.isLeaf())))) {
      _other.partyId = this.partyId;
    }
    final PropertyTree collectionExerciseIdPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("collectionExerciseId"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (collectionExerciseIdPropertyTree != null)
        : ((collectionExerciseIdPropertyTree == null)
            || (!collectionExerciseIdPropertyTree.isLeaf())))) {
      _other.collectionExerciseId = this.collectionExerciseId;
    }
    final PropertyTree collectionInstrumentIdPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("collectionInstrumentId"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (collectionInstrumentIdPropertyTree != null)
        : ((collectionInstrumentIdPropertyTree == null)
            || (!collectionInstrumentIdPropertyTree.isLeaf())))) {
      _other.collectionInstrumentId = this.collectionInstrumentId;
    }
    final PropertyTree sampleUnitRefPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("sampleUnitRef"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (sampleUnitRefPropertyTree != null)
        : ((sampleUnitRefPropertyTree == null) || (!sampleUnitRefPropertyTree.isLeaf())))) {
      _other.sampleUnitRef = this.sampleUnitRef;
    }
    final PropertyTree sampleUnitTypePropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("sampleUnitType"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (sampleUnitTypePropertyTree != null)
        : ((sampleUnitTypePropertyTree == null) || (!sampleUnitTypePropertyTree.isLeaf())))) {
      _other.sampleUnitType = this.sampleUnitType;
    }
    final PropertyTree actionPlanIdPropertyTree =
        ((_propertyTree == null) ? null : _propertyTree.get("actionPlanId"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
        ? (actionPlanIdPropertyTree != null)
        : ((actionPlanIdPropertyTree == null) || (!actionPlanIdPropertyTree.isLeaf())))) {
      _other.actionPlanId = this.actionPlanId;
    }
    final PropertyTree activeEnrolmentPropertyTree =
            ((_propertyTree == null) ? null : _propertyTree.get("activeEnrolment"));
    if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
            ? (activeEnrolmentPropertyTree != null)
            : ((activeEnrolmentPropertyTree == null) || (!activeEnrolmentPropertyTree.isLeaf())))) {
      _other.activeEnrolment = this.activeEnrolment;
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
      final CaseCreation _other,
      final PropertyTree _propertyTree,
      final PropertyTreeUse _propertyTreeUse) {
    final Builder<_B> _newBuilder = new Builder<_B>(null, null, false);
    _other.copyTo(_newBuilder, _propertyTree, _propertyTreeUse);
    return _newBuilder;
  }

  public static Builder<Void> copyExcept(
      final CaseCreation _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.EXCLUDE);
  }

  public static Builder<Void> copyOnly(
      final CaseCreation _other, final PropertyTree _propertyTree) {
    return copyOf(_other, _propertyTree, PropertyTreeUse.INCLUDE);
  }

  public static class Builder<_B> implements Buildable {

    protected final _B _parentBuilder;
    protected final CaseCreation _storedValue;
    private String partyId;
    private String collectionExerciseId;
    private String collectionInstrumentId;
    private String sampleUnitRef;
    private String sampleUnitType;
    private String actionPlanId;
    private boolean activeEnrolment;

    public Builder(final _B _parentBuilder, final CaseCreation _other, final boolean _copy) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          this.partyId = _other.partyId;
          this.collectionExerciseId = _other.collectionExerciseId;
          this.collectionInstrumentId = _other.collectionInstrumentId;
          this.sampleUnitRef = _other.sampleUnitRef;
          this.sampleUnitType = _other.sampleUnitType;
          this.actionPlanId = _other.actionPlanId;
          this.activeEnrolment = _other.activeEnrolment;
        } else {
          _storedValue = _other;
        }
      } else {
        _storedValue = null;
      }
    }

    public Builder(
        final _B _parentBuilder,
        final CaseCreation _other,
        final boolean _copy,
        final PropertyTree _propertyTree,
        final PropertyTreeUse _propertyTreeUse) {
      this._parentBuilder = _parentBuilder;
      if (_other != null) {
        if (_copy) {
          _storedValue = null;
          final PropertyTree partyIdPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("partyId"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (partyIdPropertyTree != null)
              : ((partyIdPropertyTree == null) || (!partyIdPropertyTree.isLeaf())))) {
            this.partyId = _other.partyId;
          }
          final PropertyTree collectionExerciseIdPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("collectionExerciseId"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (collectionExerciseIdPropertyTree != null)
              : ((collectionExerciseIdPropertyTree == null)
                  || (!collectionExerciseIdPropertyTree.isLeaf())))) {
            this.collectionExerciseId = _other.collectionExerciseId;
          }
          final PropertyTree collectionInstrumentIdPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("collectionInstrumentId"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (collectionInstrumentIdPropertyTree != null)
              : ((collectionInstrumentIdPropertyTree == null)
                  || (!collectionInstrumentIdPropertyTree.isLeaf())))) {
            this.collectionInstrumentId = _other.collectionInstrumentId;
          }
          final PropertyTree sampleUnitRefPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("sampleUnitRef"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (sampleUnitRefPropertyTree != null)
              : ((sampleUnitRefPropertyTree == null) || (!sampleUnitRefPropertyTree.isLeaf())))) {
            this.sampleUnitRef = _other.sampleUnitRef;
          }
          final PropertyTree sampleUnitTypePropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("sampleUnitType"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (sampleUnitTypePropertyTree != null)
              : ((sampleUnitTypePropertyTree == null) || (!sampleUnitTypePropertyTree.isLeaf())))) {
            this.sampleUnitType = _other.sampleUnitType;
          }
          final PropertyTree actionPlanIdPropertyTree =
              ((_propertyTree == null) ? null : _propertyTree.get("actionPlanId"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
              ? (actionPlanIdPropertyTree != null)
              : ((actionPlanIdPropertyTree == null) || (!actionPlanIdPropertyTree.isLeaf())))) {
            this.actionPlanId = _other.actionPlanId;
          }
          final PropertyTree activeEnrolmentPropertyTree =
                  ((_propertyTree == null) ? null : _propertyTree.get("activeEnrolment"));
          if (((_propertyTreeUse == PropertyTreeUse.INCLUDE)
                  ? (activeEnrolmentPropertyTree != null)
                  : ((activeEnrolmentPropertyTree == null) || (!activeEnrolmentPropertyTree.isLeaf())))) {
            this.activeEnrolment = _other.activeEnrolment;
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

    protected <_P extends CaseCreation> _P init(final _P _product) {
      _product.partyId = this.partyId;
      _product.collectionExerciseId = this.collectionExerciseId;
      _product.collectionInstrumentId = this.collectionInstrumentId;
      _product.sampleUnitRef = this.sampleUnitRef;
      _product.sampleUnitType = this.sampleUnitType;
      _product.actionPlanId = this.actionPlanId;
      _product.activeEnrolment = this.activeEnrolment;
      return _product;
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

    /**
     * Sets the new value of "collectionExerciseId" (any previous value will be replaced)
     *
     * @param collectionExerciseId New value of the "collectionExerciseId" property.
     */
    public Builder<_B> withCollectionExerciseId(final String collectionExerciseId) {
      this.collectionExerciseId = collectionExerciseId;
      return this;
    }

    /**
     * Sets the new value of "collectionInstrumentId" (any previous value will be replaced)
     *
     * @param collectionInstrumentId New value of the "collectionInstrumentId" property.
     */
    public Builder<_B> withCollectionInstrumentId(final String collectionInstrumentId) {
      this.collectionInstrumentId = collectionInstrumentId;
      return this;
    }

    /**
     * Sets the new value of "sampleUnitRef" (any previous value will be replaced)
     *
     * @param sampleUnitRef New value of the "sampleUnitRef" property.
     */
    public Builder<_B> withSampleUnitRef(final String sampleUnitRef) {
      this.sampleUnitRef = sampleUnitRef;
      return this;
    }

    /**
     * Sets the new value of "sampleUnitType" (any previous value will be replaced)
     *
     * @param sampleUnitType New value of the "sampleUnitType" property.
     */
    public Builder<_B> withSampleUnitType(final String sampleUnitType) {
      this.sampleUnitType = sampleUnitType;
      return this;
    }

    /**
     * Sets the new value of "actionPlanId" (any previous value will be replaced)
     *
     * @param actionPlanId New value of the "actionPlanId" property.
     */
    public Builder<_B> withActionPlanId(final String actionPlanId) {
      this.actionPlanId = actionPlanId;
      return this;
    }

    public Builder<_B> withActiveEnrolment(final boolean activeEnrolment) {
      this.activeEnrolment = activeEnrolment;
      return this;
    }

    @Override
    public CaseCreation build() {
      if (_storedValue == null) {
        return this.init(new CaseCreation());
      } else {
        return ((CaseCreation) _storedValue);
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

    private com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> partyId = null;
    private com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> collectionExerciseId =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> collectionInstrumentId =
        null;
    private com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> sampleUnitRef = null;
    private com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> sampleUnitType = null;
    private com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> actionPlanId = null;
    private com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> activeEnrolment = null;

    public Selector(final TRoot root, final TParent parent, final String propertyName) {
      super(root, parent, propertyName);
    }

    @Override
    public Map<String, PropertyTree> buildChildren() {
      final Map<String, PropertyTree> products = new HashMap<String, PropertyTree>();
      products.putAll(super.buildChildren());
      if (this.partyId != null) {
        products.put("partyId", this.partyId.init());
      }
      if (this.collectionExerciseId != null) {
        products.put("collectionExerciseId", this.collectionExerciseId.init());
      }
      if (this.collectionInstrumentId != null) {
        products.put("collectionInstrumentId", this.collectionInstrumentId.init());
      }
      if (this.sampleUnitRef != null) {
        products.put("sampleUnitRef", this.sampleUnitRef.init());
      }
      if (this.sampleUnitType != null) {
        products.put("sampleUnitType", this.sampleUnitType.init());
      }
      if (this.actionPlanId != null) {
        products.put("actionPlanId", this.actionPlanId.init());
      }
      if (this.activeEnrolment != null) {
        products.put("activeEnrolment", this.activeEnrolment.init());
      }
      return products;
    }

    public com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> partyId() {
      return ((this.partyId == null)
          ? this.partyId =
              new com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>>(
                  this._root, this, "partyId")
          : this.partyId);
    }

    public com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> collectionExerciseId() {
      return ((this.collectionExerciseId == null)
          ? this.collectionExerciseId =
              new com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>>(
                  this._root, this, "collectionExerciseId")
          : this.collectionExerciseId);
    }

    public com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> collectionInstrumentId() {
      return ((this.collectionInstrumentId == null)
          ? this.collectionInstrumentId =
              new com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>>(
                  this._root, this, "collectionInstrumentId")
          : this.collectionInstrumentId);
    }

    public com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> sampleUnitRef() {
      return ((this.sampleUnitRef == null)
          ? this.sampleUnitRef =
              new com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>>(
                  this._root, this, "sampleUnitRef")
          : this.sampleUnitRef);
    }

    public com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> sampleUnitType() {
      return ((this.sampleUnitType == null)
          ? this.sampleUnitType =
              new com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>>(
                  this._root, this, "sampleUnitType")
          : this.sampleUnitType);
    }

    public com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> actionPlanId() {
      return ((this.actionPlanId == null)
          ? this.actionPlanId =
              new com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>>(
                  this._root, this, "actionPlanId")
          : this.actionPlanId);
    }

    public com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>> activeEnrolment() {
      return ((this.activeEnrolment == null)
              ? this.activeEnrolment =
              new com.kscs.util.jaxb.Selector<TRoot, Selector<TRoot, TParent>>(
                      this._root, this, "activeEnrolment")
              : this.activeEnrolment);
    }
  }
}

package org.healthnlp.deepphe.neo4j.node;


import java.util.List;

/**
 * @author SPF , chip-nlp
 * @version %I%
 * @since 7/15/2020
 */
public class NeoplasmSummary {

   private String id;
   private String site_major;
   private String site_minor;
   private String site_related;
   private String laterality;
   private List<NeoplasmAttribute> attributes;

   private String topography_major;
   private String topography_minor;
   private String histology;
   private String behavior;
   private String laterality_code;
   private String grade;
   private String pathologic_t;
   private String pathologic_n;
   private String pathologic_m;
   private String er;
   private String pr;
   private String her2;
   private String ki67;
   private String msi;
   private String kras;
   private String psa;


   public String getId() {
      return id;
   }

   public void setId( final String id ) {
      this.id = id;
   }

   public List<NeoplasmAttribute> getAttributes() {
      return attributes;
   }

   public void setAttributes( final List<NeoplasmAttribute> attributes ) {
      this.attributes = attributes;
   }


   public String getSite_major() {
      return site_major;
   }

   public void setSite_major( final String site_major ) {
      this.site_major = site_major;
   }

   public String getSite_minor() {
      return site_minor;
   }

   public void setSite_minor( final String site_minor ) {
      this.site_minor = site_minor;
   }

   public String getSite_related() {
      return site_related;
   }

   public void setSite_related( final String site_related ) {
      this.site_related = site_related;
   }

   public String getTopography_major() {
      return topography_major;
   }

   public void setTopography_major( final String topography_major ) {
      this.topography_major = topography_major;
   }

   public String getTopography_minor() {
      return topography_minor;
   }

   public void setTopography_minor( final String topography_minor ) {
      this.topography_minor = topography_minor;
   }

   public String getHistology() {
      return histology;
   }

   public void setHistology( final String histology ) {
      this.histology = histology;
   }

   public String getBehavior() {
      return behavior;
   }

   public void setBehavior( final String behavior ) {
      this.behavior = behavior;
   }

   public String getLaterality() {
      return laterality;
   }

   public void setLaterality( final String laterality ) {
      this.laterality = laterality;
   }

   public String getLaterality_code() {
      return laterality_code;
   }

   public void setLaterality_code( final String laterality_code ) {
      this.laterality_code = laterality_code;
   }

   public String getGrade() {
      return grade;
   }

   public void setGrade( final String grade ) {
      this.grade = grade;
   }

   public String getPathologic_t() {
      return pathologic_t;
   }

   public void setPathologic_t( final String pathologic_t ) {
      this.pathologic_t = pathologic_t;
   }

   public String getPathologic_n() {
      return pathologic_n;
   }

   public void setPathologic_n( final String pathologic_n ) {
      this.pathologic_n = pathologic_n;
   }

   public String getPathologic_m() {
      return pathologic_m;
   }

   public void setPathologic_m( final String pathologic_m ) {
      this.pathologic_m = pathologic_m;
   }

   public String getEr() {
      return er;
   }

   public void setEr( final String er ) {
      this.er = er;
   }

   public String getPr() {
      return pr;
   }

   public void setPr( final String pr ) {
      this.pr = pr;
   }

   public String getHer2() {
      return her2;
   }

   public void setHer2( final String her2 ) {
      this.her2 = her2;
   }

   public String getKi67() {
      return ki67;
   }

   public void setKi67( final String ki67 ) {
      this.ki67 = ki67;
   }

   public String getMsi() {
      return msi;
   }

   public void setMsi( final String msi ) {
      this.msi = msi;
   }

   public String getKras() {
      return kras;
   }

   public void setKras( final String kras ) {
      this.kras = kras;
   }

   public String getPsa() {
      return psa;
   }

   public void setPsa( final String psa ) {
      this.psa = psa;
   }




}

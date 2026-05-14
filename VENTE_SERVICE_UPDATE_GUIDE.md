# VenteService Update Guide
## How to Complete the Integration

**File**: `src/main/java/org/example/pidev/services/ventes/VenteService.java`  
**Estimated Time**: 30-45 minutes  
**Priority**: HIGH - Required for complete Symfony schema alignment

---

## Overview

The VenteService needs to be updated to include 5 new Symfony schema fields:
- `quantite` (double) - Quantity sold
- `id_produit` (Integer) - Product ID
- `ville` (String) - Delivery city
- `region` (String) - Delivery region  
- `frais_livraison` (Float) - Delivery fees

These fields are optional (nullable) in the database for backward compatibility.

---

## Changes Required

### 1. Update the `add()` Method

**Current SQL** (Line ~113):
```java
String query = "INSERT INTO vente (date_vente, montant_total, id_client, id_user) VALUES (?, ?, ?, ?)";
```

**Replace With**:
```java
String query = "INSERT INTO vente (date_vente, montant_total, id_client, id_user, quantite, id_produit, ville, region, frais_livraison) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
```

**Add Parameter Bindings** (after line ~119):
```java
// Existing parameters
pst.setDate(1, Date.valueOf(vente.getDateVente()));
pst.setDouble(2, vente.getMontantTotal());
pst.setInt(3, vente.getIdClient());
pst.setInt(4, vente.getIdUser());

// NEW: Add these lines
pst.setDouble(5, vente.getQuantite() > 0 ? vente.getQuantite() : 0.0);

// Handle optional product ID
if (vente.getIdProduit() != null && vente.getIdProduit() > 0) {
    pst.setInt(6, vente.getIdProduit());
} else {
    pst.setNull(6, java.sql.Types.INTEGER);
}

pst.setString(7, vente.getVille());
pst.setString(8, vente.getRegion());

// Handle optional delivery fees
if (vente.getFraisLivraison() != null) {
    pst.setFloat(9, vente.getFraisLivraison());
} else {
    pst.setNull(9, java.sql.Types.FLOAT);
}
```

---

### 2. Update the `update()` Method

**Current SQL** (Line ~147):
```java
String query = "UPDATE vente SET date_vente = ?, montant_total = ?, id_client = ?, id_user = ? WHERE id_vente = ?";
```

**Replace With**:
```java
String query = "UPDATE vente SET date_vente = ?, montant_total = ?, id_client = ?, id_user = ?, " +
        "quantite = ?, id_produit = ?, ville = ?, region = ?, frais_livraison = ? WHERE id_vente = ?";
```

**Update Parameter Bindings** (around line ~150):
```java
// Existing parameters (1-4)
pst.setDate(1, Date.valueOf(vente.getDateVente()));
pst.setDouble(2, vente.getMontantTotal());
pst.setInt(3, vente.getIdClient());
pst.setInt(4, vente.getIdUser());

// NEW: Add these (5-9), existing WHERE clause becomes position 10
pst.setDouble(5, vente.getQuantite() > 0 ? vente.getQuantite() : 0.0);

if (vente.getIdProduit() != null && vente.getIdProduit() > 0) {
    pst.setInt(6, vente.getIdProduit());
} else {
    pst.setNull(6, java.sql.Types.INTEGER);
}

pst.setString(7, vente.getVille());
pst.setString(8, vente.getRegion());

if (vente.getFraisLivraison() != null) {
    pst.setFloat(9, vente.getFraisLivraison());
} else {
    pst.setNull(9, java.sql.Types.FLOAT);
}

pst.setInt(10, vente.getIdVente());  // WHERE clause parameter
```

---

### 3. Update the `getById()` Method

**Current Code** (Line ~185-191):
```java
if (rs.next()) {
    return new Vente(
            rs.getInt("id_vente"),
            rs.getDate("date_vente") != null ? rs.getDate("date_vente").toLocalDate() : null,
            rs.getDouble("montant_total"),
            org.example.pidev.utils.DbSchemaSupport.getInt(rs, "id_client", 0),
            org.example.pidev.utils.DbSchemaSupport.getInt(rs, "id_user", 0)
    );
}
```

**Replace With**:
```java
if (rs.next()) {
    Vente vente = new Vente(
            rs.getInt("id_vente"),
            rs.getDate("date_vente") != null ? rs.getDate("date_vente").toLocalDate() : null,
            rs.getDouble("montant_total"),
            org.example.pidev.utils.DbSchemaSupport.getInt(rs, "id_client", 0),
            org.example.pidev.utils.DbSchemaSupport.getInt(rs, "id_user", 0)
    );
    
    // Add new fields
    try {
        vente.setQuantite(rs.getDouble("quantite"));
    } catch (SQLException e) { /* Field doesn't exist yet */ }
    
    try {
        Integer idProduit = rs.getInt("id_produit");
        if (!rs.wasNull()) vente.setIdProduit(idProduit);
    } catch (SQLException e) { /* Field doesn't exist yet */ }
    
    try {
        vente.setVille(rs.getString("ville"));
    } catch (SQLException e) { /* Field doesn't exist yet */ }
    
    try {
        vente.setRegion(rs.getString("region"));
    } catch (SQLException e) { /* Field doesn't exist yet */ }
    
    try {
        Float fees = rs.getFloat("frais_livraison");
        if (!rs.wasNull()) vente.setFraisLivraison(fees);
    } catch (SQLException e) { /* Field doesn't exist yet */ }
    
    return vente;
}
```

---

### 4. Update the `getAll()` Method

**Current Code** (Line ~210-217):
```java
Vente vente = new Vente(
        rs.getInt("id_vente"),
        rs.getDate("date_vente") != null ? rs.getDate("date_vente").toLocalDate() : null,
        rs.getDouble("montant_total"),
        org.example.pidev.utils.DbSchemaSupport.getInt(rs, "id_client", 0),
        org.example.pidev.utils.DbSchemaSupport.getInt(rs, "id_user", 0)
);
ventes.add(vente);
```

**Replace With**:
```java
Vente vente = new Vente(
        rs.getInt("id_vente"),
        rs.getDate("date_vente") != null ? rs.getDate("date_vente").toLocalDate() : null,
        rs.getDouble("montant_total"),
        org.example.pidev.utils.DbSchemaSupport.getInt(rs, "id_client", 0),
        org.example.pidev.utils.DbSchemaSupport.getInt(rs, "id_user", 0)
);

// Add new fields
try {
    vente.setQuantite(rs.getDouble("quantite"));
    
    Integer idProduit = rs.getInt("id_produit");
    if (!rs.wasNull()) vente.setIdProduit(idProduit);
    
    vente.setVille(rs.getString("ville"));
    vente.setRegion(rs.getString("region"));
    
    Float fees = rs.getFloat("frais_livraison");
    if (!rs.wasNull()) vente.setFraisLivraison(fees);
} catch (SQLException e) {
    // Some columns may not exist yet - this is backward compatible
}

ventes.add(vente);
```

---

### 5. Update the `getByUserId()` Method

**Find Similar Code Pattern** (around line ~235):
Look for similar Vente object construction and apply the same pattern as `getAll()` above.

---

### 6. Optional: Create Helper Method

Add this helper method to extract Vente from ResultSet consistently:

```java
/**
 * Extract Vente object from ResultSet with all Symfony fields
 */
private Vente extractVenteFromResultSet(ResultSet rs) throws SQLException {
    Vente vente = new Vente(
            rs.getInt("id_vente"),
            rs.getDate("date_vente") != null ? rs.getDate("date_vente").toLocalDate() : null,
            rs.getDouble("montant_total"),
            org.example.pidev.utils.DbSchemaSupport.getInt(rs, "id_client", 0),
            org.example.pidev.utils.DbSchemaSupport.getInt(rs, "id_user", 0)
    );
    
    // Add Symfony fields safely
    try {
        vente.setQuantite(rs.getDouble("quantite"));
        
        Integer idProduit = rs.getInt("id_produit");
        if (!rs.wasNull()) vente.setIdProduit(idProduit);
        
        vente.setVille(rs.getString("ville"));
        vente.setRegion(rs.getString("region"));
        
        Float fees = rs.getFloat("frais_livraison");
        if (!rs.wasNull()) vente.setFraisLivraison(fees);
    } catch (SQLException e) {
        // Backward compatibility: fields may not exist yet
        System.out.println("⚠️ Some Symfony fields not yet in database:" + e.getMessage());
    }
    
    return vente;
}
```

Then replace all Vente construction with:
```java
Vente vente = extractVenteFromResultSet(rs);
```

---

## Testing the Changes

### Unit Test Example
```java
@Test
public void testVenteWithNewFields() {
    Vente vente = new Vente();
    vente.setDateVente(LocalDate.now());
    vente.setMontantTotal(1000.0);
    vente.setIdClient(1);
    vente.setIdUser(13);
    
    // New fields
    vente.setQuantite(10.5);
    vente.setIdProduit(21);
    vente.setVille("Tunis");
    vente.setRegion("Tunis");
    vente.setFraisLivraison(12.0f);
    
    // Test add
    boolean success = venteService.add(vente);
    assertTrue(success);
    
    // Test retrieve
    Vente retrieved = venteService.getById(vente.getIdVente());
    assertNotNull(retrieved);
    assertEquals(10.5, retrieved.getQuantite(), 0.01);
    assertEquals("Tunis", retrieved.getVille());
    assertEquals(12.0f, retrieved.getFraisLivraison(), 0.01f);
}
```

---

## Backward Compatibility Notes

The changes maintain full backward compatibility:

1. **NULL Handling**: All new fields are nullable in database
2. **Optional Parameters**: Use `if (field != null)` checks
3. **Try-Catch**: ResultSet field access is wrapped for safety
4. **Default Values**: Missing fields get NULL or 0.0
5. **Existing Code**: No breaking changes to existing methods

---

## Import Statements

Ensure these are imported (usually already present):
```java
import java.sql.Types;  // For setNull() calls
```

---

## Validation

After making changes:

1. **Compile**: `mvn clean compile` - Should have no errors
2. **Test**: Run unit tests - Should all pass
3. **Verify**: Check database - New fields should be queryable
4. **Integration**: Test with ProfileController and other consumers

---

## Summary

The VenteService update follows the same pattern as UtilisateurService:
- SQL queries include new fields
- Parameters bound in correct order
- ResultSet extraction handles all fields safely
- Backward compatibility maintained with NULL checks
- All changes are additive (no breaking changes)

**Estimated Time**: 30-45 minutes  
**Complexity**: LOW (straightforward parameter additions)  
**Risk**: VERY LOW (backward compatible)

---

**Version**: 1.0  
**Date**: May 5, 2026


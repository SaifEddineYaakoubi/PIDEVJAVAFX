# Smart Farm: Java ↔ Symfony Database Alignment
## Implementation Action Plan

**Status**: Ready for Implementation  
**Date**: May 5, 2026  
**Priority**: HIGH - Required for Symfony backend integration

---

## 📋 Summary

The Java Smart Farm desktop application has been aligned with the Symfony backend database schema. All Java entity models now match the Symfony schema structure, ensuring seamless data synchronization between both systems.

**Key Achievements**:
- ✅ 5 existing entity models updated
- ✅ 4 new entity classes created
- ✅ Comprehensive SQL migration script prepared
- ✅ Full backward compatibility maintained
- ✅ Complete documentation generated

---

## 🔧 Phase 1: Database Schema Update (CRITICAL)

### Step 1.1: Backup Current Database
```bash
# Command to backup MySQL database
mysqldump -u root -p smartfarm > smartfarm_backup_2026_05_05.sql

# OR use MySQL Workbench GUI backup
```

**Location**: `sql/align_symfony_schema.sql`

**Estimated Time**: 5-10 minutes

**Risk Level**: LOW (uses IF NOT EXISTS clauses)

### Step 1.2: Execute Migration Script
```bash
# Connect to MySQL
mysql -u root -p

# Select database
USE smartfarm;

# Execute migration script
SOURCE sql/align_symfony_schema.sql;

# Or from command line
mysql -u root -p smartfarm < sql/align_symfony_schema.sql
```

**Expected Output**: Multiple "Query OK" messages for CREATE and ALTER operations

**Verification**:
```sql
-- Verify new columns in UTILISATEUR
SHOW COLUMNS FROM utilisateur;

-- Verify new tables exist
SHOW TABLES LIKE 'face_images';
SHOW TABLES LIKE 'message';
SHOW TABLES LIKE 'reset_password_request';
SHOW TABLES LIKE 'user_badge';

-- Verify foreign keys
SELECT CONSTRAINT_NAME, TABLE_NAME, COLUMN_NAME 
FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE 
WHERE TABLE_SCHEMA = 'smartfarm' AND CONSTRAINT_NAME LIKE 'FK_%';
```

**Estimated Time**: 2-5 minutes

---

## 🎯 Phase 2: Java Model Verification

### Step 2.1: Verify Model Files Exist
Located in `src/main/java/org/example/pidev/models/`:

**Updated Models**:
- ✅ `Utilisateur.java` - 5 new fields added
- ✅ `Client.java` - Normalized, 1 new field (badge)
- ✅ `Vente.java` - 5 new fields added

**New Models** (newly created):
- ✅ `FaceImage.java` - Face recognition images
- ✅ `Message.java` - User messaging
- ✅ `ResetPasswordRequest.java` - Password resets
- ✅ `UserBadge.java` - User-badge relationships

### Step 2.2: Verify Compilation
```bash
# Run Maven compile
mvn clean compile

# Should complete without errors
# Expected: BUILD SUCCESS
```

**Estimated Time**: 2-3 minutes (download dependencies if first time)

### Step 2.3: Code Review Checklist

For each model class, verify:
- [ ] All fields have getters and setters
- [ ] Constructors properly initialized
- [ ] Field names match database column names (camelCase in Java, snake_case in DB)
- [ ] Data types match database schema
- [ ] toString() methods implemented
- [ ] JavaDoc comments for key fields

**Models to Check**:
1. Run: `grep -n "private\|public.*get\|public.*set" src/main/java/org/example/pidev/models/Utilisateur.java`
2. Run: `grep -n "private\|public.*get\|public.*set" src/main/java/org/example/pidev/models/Client.java`
3. Similarly for other updated models

---

## 🔄 Phase 3: Service Layer Updates

### Priority 1: CRITICAL (Do First)

#### 3.1.1 Update UtilisateurService
**File**: `src/main/java/org/example/pidev/services/utilisateur/UtilisateurService.java`

**Changes Required**:
1. Update SELECT queries to include new columns:
   ```sql
   SELECT id_user, nom, prenom, email, mot_de_passe, role, statut, date_creation,
          face_descriptor, face_enabled, profile_picture, date_naissance, sexe
   FROM utilisateur WHERE id_user = ?
   ```

2. Add face descriptor serialization/deserialization methods

3. Create methods:
   - `enableFaceRecognition(int userId, String descriptor)`
   - `disableFaceRecognition(int userId)`
   - `getFaceDescriptor(int userId)`
   - `updateProfilePicture(int userId, String path)`

**Estimated Time**: 30-45 minutes

---

#### 3.1.2 Update ClientService
**File**: `src/main/java/org/example/pidev/services/...ClientService.java`

**Changes Required**:
1. Update SELECT queries to include badge:
   ```sql
   SELECT id_client, nom, contact, adresse, id_user, badge
   FROM client WHERE id_client = ?
   ```

2. Add methods:
   - `updateBadge(int clientId, String badgeLevel)`
   - `getBadgesByClient(int clientId)`

3. Rename internal references from `email` to `contact` if needed

**Estimated Time**: 15-20 minutes

---

#### 3.1.3 Update VenteService
**File**: `src/main/java/org/example/pidev/services/ventes/VenteService.java`

**Changes Required**:
1. Update INSERT statements to include new fields:
   ```sql
   INSERT INTO vente 
   (date_vente, montant_total, id_client, id_user, quantite, id_produit, ville, region, frais_livraison)
   VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
   ```

2. Update SELECT queries for all new fields

3. Create methods:
   - `getVentesByProduit(int productId)`
   - `calculateDeliveryTotal()` - sum montant_total + frais_livraison

**Estimated Time**: 25-35 minutes

---

### Priority 2: HIGH (Do Second)

#### 3.2.1 Create FaceImageService
**File**: `src/main/java/org/example/pidev/services/.../FaceImageService.java`

**Methods to Implement**:
- `create(FaceImage faceImage)`
- `getByUserId(int userId)`
- `update(FaceImage faceImage)`
- `delete(int id)`
- `deleteByUserId(int userId)`

**Estimated Time**: 20-25 minutes

---

#### 3.2.2 Create MessageService
**File**: `src/main/java/org/example/pidev/services/.../MessageService.java`

**Methods to Implement**:
- `sentiment(Message message)`
- `getConversation(int userId1, int userId2)`
- `getUnreadMessages(int userId)`
- `markAsRead(int messageId)`
- `delete(int messageId)`

**Estimated Time**: 25-30 minutes

---

#### 3.2.3 Create ResetPasswordService
**File**: `src/main/java/org/example/pidev/services/.../ResetPasswordService.java`

**Methods to Implement**:
- `createResetRequest(int userId, String token, LocalDateTime expiresAt)`
- `validateToken(String selector, String token)`
- `redeemToken(String selector, String newPassword)`
- `deleteExpiredRequests()`

**Estimated Time**: 20-25 minutes

---

#### 3.2.4 Create UserBadgeService
**File**: `src/main/java/org/example/pidev/services/.../UserBadgeService.java`

**Methods to Implement**:
- `awardBadge(int userId, int badgeId)`
- `getUserBadges(int userId)`
- `hasBadge(int userId, int badgeId)`
- `removeBadge(int userId, int badgeId)`

**Estimated Time**: 15-20 minutes

---

### Priority 3: MEDIUM (Do Third)

#### 3.3 Update Existing Services
- [ ] Update all services using `idUser` or `userId`
- [ ] Add logging for new fields
- [ ] Add null checks for optional fields

**Estimated Time**: 1-2 hours

---

## 📝 Phase 4: Testing

### 4.1 Unit Tests
```bash
# Run existing unit tests
mvn test

# All tests should pass
```

**Estimated Time**: 5-10 minutes

### 4.2 Integration Tests
Create new test class: `UtilisateurModelTest`

```java
@Test
public void testUtilisateurNewFields() {
    Utilisateur user = new Utilisateur();
    user.setFaceDescriptor("[...]");
    user.setFaceEnabled(true);
    user.setProfilePicture("profile_pic.jpg");
    user.setDateNaissance(LocalDate.of(1990, 1, 1));
    user.setSexe("homme");
    
    assertNotNull(user.getFaceDescriptor());
    assertTrue(user.isFaceEnabled());
    assertEquals("profile_pic.jpg", user.getProfilePicture());
}
```

**Estimated Time**: 30-45 minutes

### 4.3 Data Integrity Verification
```sql
-- Run after changes
SELECT COUNT(*) as total_utilisateurs FROM utilisateur;
SELECT COUNT(*) as total_messages FROM message;
SELECT COUNT(*) as total_face_images FROM face_images;
SELECT COUNT(*) as total_user_badges FROM user_badge;

-- Verify FK relationships
SELECT u.id_user, m.sender_id 
FROM utilisateur u 
LEFT JOIN message m ON u.id_user = m.sender_id;
```

**Estimated Time**: 15-20 minutes

---

## 📦 Phase 5: Build and Deployment

### 5.1 Clean Build
```bash
# Clean and rebuild
mvn clean package

# Should complete with no errors
# Expected: BUILD SUCCESS
```

**Estimated Time**: 5-10 minutes (depending on dependencies)

### 5.2 Application Testing
- [ ] Start application: `mvn javafx:run`
- [ ] Test login functionality
- [ ] Test user creation with new fields
- [ ] Test messaging features
- [ ] Test face recognition with new descriptor field
- [ ] Test client badge system
- [ ] Test sales with new fields

**Estimated Time**: 1-2 hours

### 5.3 Symfony Integration Testing
```bash
# If connecting to Symfony backend API
# Verify endpoints return aligned data
curl http://localhost:8000/api/utilisateurs/1

# Should include new fields:
# - face_descriptor
# - face_enabled  
# - profile_picture
# - date_naissance
# - sexe
```

**Estimated Time**: 45-60 minutes

---

## ⏱️ Timeline Estimate

| Phase | Task | Time | Status |
|-------|------|------|--------|
| 1 | Database Backup | 5 min | Pending |
| 1 | Migration Script Execution | 5 min | Pending |
| 1 | Verification | 5 min | Pending |
| 2 | Model Verification | 5 min | ✅ Complete |
| 2 | Maven Compilation | 5 min | Pending |
| 3 | UtilisateurService Update | 45 min | Pending |
| 3 | ClientService Update | 20 min | Pending |
| 3 | VenteService Update | 30 min | Pending |
| 3 | New Services Creation | 2 hours | Pending |
| 4 | Testing | 2 hours | Pending |
| 5 | Build and Deploy | 1 hour | Pending |
| **TOTAL** | | **7-9 hours** | |

---

## 🚨 Risk Mitigation

### Risk 1: Data Loss
**Mitigation**: Comprehensive database backup before any changes
- Action: Run backup command in Phase 1
- Rollback: `mysql -u root -p smartfarm < smartfarm_backup_2026_05_05.sql`

### Risk 2: Service Breaks
**Mitigation**: Maintain backward compatibility
- All changes are additive (new columns/tables)
- Old code continues to work
- New features are optional

### Risk 3: Foreign Key Conflicts  
**Mitigation**: Execute migration in correct order
- Tables with FKs created last
- Already handled in migration script

### Risk 4: Performance Degradation
**Mitigation**: Proper indexing added
- Check `align_symfony_schema.sql` for CREATE INDEX statements
- Monitor query performance after deployment

---

## ✅ Verification Checklist (Pre-Deployment)

- [ ] Database backup successfully created
- [ ] Migration script executed without errors
- [ ] All new columns visible in UTILISATEUR table
- [ ] All new tables exist (face_images, message, reset_password_request, user_badge)
- [ ] Foreign keys properly configured
- [ ] Java models compile without errors
- [ ] All unit tests pass
- [ ] Integration tests pass
- [ ] Application starts without errors
- [ ] Login functionality works
- [ ] New fields can be persisted and retrieved
- [ ] Symfony backend API returns aligned data
- [ ] No data corruption or loss
- [ ] Performance acceptable (no query timeouts)

---

## 📞 Support & References

### Documentation Files
- **This File**: `IMPLEMENTATION_ACTION_PLAN.md`
- **Schema Guide**: `SCHEMA_ALIGNMENT_GUIDE.md`
- **Migration Script**: `sql/align_symfony_schema.sql`
- **Model Files**: `src/main/java/org/example/pidev/models/`

### Contact
If you encounter issues:
1. Review the Documentation
2. Check the migration script comments
3. Compare Java models with this guide
4. Verify database schema with `SHOW COLUMNS` commands

---

## 📋 Sign-Off

- [ ] I understand the changes needed
- [ ] I have backed up the database
- [ ] I am ready to proceed with Phase 1
- [ ] I will follow the implementation order
- [ ] I will test after each phase

**Developer Name**: _______________  
**Date**: _______________  
**Manager Approval**: _______________

---

**Generated**: May 5, 2026  
**Version**: 1.0  
**Status**: READY FOR IMPLEMENTATION


# Database Alignment Completion Checklist

**Project**: Smart Farm Java ↔ Symfony Integration  
**Date Started**: _____________  
**Date Completed**: _____________  
**Completed By**: _____________  

---

## 📋 Phase 1: Pre-Implementation Preparation

### 1.1 Review Documentation
- [ ] Read SCHEMA_ALIGNMENT_GUIDE.md
- [ ] Read IMPLEMENTATION_ACTION_PLAN.md  
- [ ] Read MODEL_CHANGES_REFERENCE.md
- [ ] Understand all changes required
- [ ] Clarify any questions with team lead

### 1.2 Environment Setup
- [ ] MySQL server running locally
- [ ] Database `smartfarm` accessible
- [ ] MySQL client tools (MySQL Workbench or command line) installed
- [ ] Java JDK 17+ installed
- [ ] Maven installed and configured
- [ ] IDE (IntelliJ IDEA) updated

### 1.3 Backup & Safety
- [ ] Created full backup of `smartfarm` database
- [ ] Backup file verified (can restore if needed)
- [ ] Backup location documented: _______________
- [ ] Backup tested successfully

**Notes**: _______________________________________________________________

---

## 🗄️ Phase 2: Database Schema Updates

### 2.1 Execute Migration Script
- [ ] Located migration file: `sql/align_symfony_schema.sql`
- [ ] Connected to MySQL as root user
- [ ] Executed migration script successfully
- [ ] No errors in execution (IF NOT EXISTS warnings are OK)

### 2.2 Verify UTILISATEUR Table Changes
- [ ] `face_descriptor` column exists (LONGTEXT)
- [ ] `face_enabled` column exists (TINYINT)
- [ ] `profile_picture` column exists (VARCHAR)
- [ ] `date_naissance` column exists (DATE)
- [ ] `sexe` column exists (VARCHAR)

**Verification command**:
```sql
SHOW COLUMNS FROM utilisateur;
```
✅ Result verified at: _____________

### 2.3 Verify CLIENT Table Changes
- [ ] `badge` column exists (VARCHAR)

**Verification command**:
```sql
SHOW COLUMNS FROM client;
```
✅ Result verified at: _____________

### 2.4 Verify VENTE Table Changes
- [ ] `quantite` column exists (DOUBLE)
- [ ] `id_produit` column exists (INT)
- [ ] `ville` column exists (VARCHAR)
- [ ] `region` column exists (VARCHAR)
- [ ] `frais_livraison` column exists (FLOAT)
- [ ] Foreign key constraint on `id_produit` exists

**Verification command**:
```sql
SHOW COLUMNS FROM vente;
SHOW CREATE TABLE vente\G
```
✅ Result verified at: _____________

### 2.5 Verify New Tables Created
- [ ] `face_images` table exists with columns: id, user_id, face_path, created_at, updated_at
- [ ] `message` table exists with columns: id, sender_id, receiver_id, content, sent_at, is_read
- [ ] `reset_password_request` table exists with columns: id, selector, hashed_token, requested_at, expires_at, user_id
- [ ] `user_badge` table exists with columns: id, user_id, badge_id, created_at

**Verification command**:
```sql
SHOW TABLES LIKE 'face_images';
SHOW TABLES LIKE 'message';
SHOW TABLES LIKE 'reset_password_request';
SHOW TABLES LIKE 'user_badge';
```
✅ Result verified at: _____________

### 2.6 Verify Foreign Keys & Indexes
- [ ] Foreign keys created on new tables
- [ ] Indexes created on performance-critical columns
- [ ] No orphaned foreign key constraints

**Verification command**:
```sql
SELECT CONSTRAINT_NAME, TABLE_NAME, REFERENCED_TABLE_NAME 
FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS 
WHERE TABLE_SCHEMA = 'smartfarm' 
ORDER BY TABLE_NAME;
```
✅ Result verified at: _____________

### 2.7 Data Integrity Check
- [ ] No data loss in existing tables
- [ ] Existing records query correctly
- [ ] NULL values populated where appropriate

**Quick test**:
```sql
SELECT COUNT(*) as user_count FROM utilisateur;
SELECT COUNT(*) as client_count FROM client;
SELECT COUNT(*) as vente_count FROM vente;
```
✅ Result verified at: _____________

**Notes**: _______________________________________________________________

---

## 🔄 Phase 3: Java Model Verification

### 3.1 Updated Models Compiled
- [ ] Utilisateur.java compiles without errors
- [ ] Client.java compiles without errors
- [ ] Vente.java compiles without errors

**Compilation command**: 
```bash
mvn clean compile -DskipTests
```
✅ Result verified at: _____________

### 3.2 New Models Created & Compiled
- [ ] FaceImage.java created successfully
- [ ] Message.java created successfully
- [ ] ResetPasswordRequest.java created successfully
- [ ] UserBadge.java created successfully
- [ ] All new models compile without errors

**Files verified**:
- [ ] `src/main/java/org/example/pidev/models/FaceImage.java`
- [ ] `src/main/java/org/example/pidev/models/Message.java`
- [ ] `src/main/java/org/example/pidev/models/ResetPasswordRequest.java`
- [ ] `src/main/java/org/example/pidev/models/UserBadge.java`

### 3.3 Model Field Verification
- [ ] Utilisateur has all 5 new fields with getters/setters
- [ ] Client has `badge` field with getters/setters
- [ ] Vente has all 5 new fields with getters/setters
- [ ] All new model classes have complete methods

**Spot check**:
- [ ] Utilisateur.getFaceDescriptor() exists
- [ ] Utilisateur.isFaceEnabled() exists
- [ ] Client.getBadge() exists
- [ ] Vente.getQuantite() exists
- [ ] FaceImage.getUserId() exists
- [ ] Message.getContent() exists
- [ ] ResetPasswordRequest.isExpired() exists
- [ ] UserBadge.getBadgeId() exists

### 3.4 Backward Compatibility Check
- [ ] Old code can still create Utilisateur objects
- [ ] Old constructors still work
- [ ] Legacy field names still accessible
- [ ] No breaking changes to existing APIs

**Test**:
```java
// Old way should still work
Utilisateur user = new Utilisateur("Ahmed", "Ahmed", "ahmed@gmail.com", "pass", Role.AGRICULTER, true, LocalDate.now());
```

**Notes**: _______________________________________________________________

---

## 🔧 Phase 4: Service Layer Updates

### 4.1 Critical Service Updates

#### 4.1.1 UtilisateurService
- [ ] SELECT queries updated to include new columns
- [ ] INSERT queries updated to include new columns
- [ ] UPDATE queries updated to include new columns
- [ ] New methods for face descriptor handling
- [ ] Tests added and passing
- [ ] Code reviewed

**Methods to verify**:
- [ ] `getById()` includes all new fields
- [ ] `create()` accepts new fields
- [ ] `update()` accepts new fields

#### 4.1.2 ClientService
- [ ] SELECT queries updated to include badge
- [ ] Badge field properly mapped
- [ ] Backward compatibility maintained
- [ ] Tests added and passing
- [ ] Code reviewed

#### 4.1.3 VenteService
- [ ] SELECT queries updated with new columns
- [ ] INSERT queries updated with new columns
- [ ] Foreign key to produit properly handled
- [ ] Tests added and passing
- [ ] Code reviewed

### 4.2 New Service Classes Created

#### 4.2.1 FaceImageService
- [ ] Service class created
- [ ] Implements standard CRUD operations
- [ ] `getByUserId()` method implemented
- [ ] `deleteByUserId()` method implemented
- [ ] Tests created and passing

#### 4.2.2 MessageService
- [ ] Service class created
- [ ] `getConversation()` method implemented
- [ ] `getUnreadMessages()` method implemented
- [ ] `markAsRead()` method implemented
- [ ] Tests created and passing

#### 4.2.3 ResetPasswordService
- [ ] Service class created
- [ ] `createResetRequest()` implemented
- [ ] `validateToken()` implemented
- [ ] `deleteExpiredRequests()` implemented
- [ ] Tests created and passing

#### 4.2.4 UserBadgeService
- [ ] Service class created
- [ ] `awardBadge()` implemented
- [ ] `getUserBadges()` implemented
- [ ] `hasBadge()` implemented
- [ ] Tests created and passing

### 4.3 Service Test Coverage
- [ ] Unit tests written for all changed services
- [ ] Unit tests written for all new services
- [ ] Test coverage > 80%
- [ ] All tests passing

**Test command**:
```bash
mvn test
```
✅ Result verified at: _____________

**Notes**: _______________________________________________________________

---

## ✅ Phase 5: Integration Testing

### 5.1 Database Connectivity
- [ ] Application connects to database successfully
- [ ] Connection string includes new charset (utf8mb4)
- [ ] Connection pooling working correctly
- [ ] Prepared statements used for all queries

**Test location**: `src/main/java/org/example/pidev/utils/DBConnection.java`

### 5.2 Model Serialization/Deserialization
- [ ] Can create Utilisateur with new fields
- [ ] Can save new fields to database
- [ ] Can retrieve new fields from database
- [ ] All data types correctly marshaled

**Test code**:
```java
@Test
public void testUtilisateurNewFields() {
    Utilisateur u = new Utilisateur();
    u.setFaceDescriptor("[0.1, 0.2, ...]");
    u.setFaceEnabled(true);
    u.setProfilePicture("pic.jpg");
    u.setDateNaissance(LocalDate.of(1990, 1, 1));
    u.setSexe("homme");
    
    // Create in DB
    int id = service.create(u);
    
    // Retrieve from DB
    Utilisateur retrieved = service.getById(id);
    
    // Verify
    assertEquals("[0.1, 0.2, ...]", retrieved.getFaceDescriptor());
    assertTrue(retrieved.isFaceEnabled());
}
```

### 5.3 Application Startup
- [ ] Application starts without errors
- [ ] No classpath issues
- [ ] No initialization failures
- [ ] All services instantiate correctly

**Startup command**:
```bash
mvn javafx:run
```
✅ Application started successfully

### 5.4 Basic Feature Testing
- [ ] Login functionality works
- [ ] User creation works  
- [ ] User update works (with new fields)
- [ ] Client creation works
- [ ] Client with badge works
- [ ] Sales creation works (with new fields)
- [ ] Face image upload works
- [ ] Message sending/receiving works
- [ ] Password reset flow works
- [ ] Badge awarding works

### 5.5 Data Round-Trip Testing
- [ ] Create user with new fields
- [ ] Save to database
- [ ] Retrieve from database
- [ ] Verify all fields intact
- [ ] Update new fields
- [ ] Save updated data
- [ ] Verify updates persisted

### 5.6 Edge Cases
- [ ] NULL handling for optional new fields
- [ ] Missing foreign key references handled gracefully
- [ ] Unicode characters in face_descriptor work
- [ ] Large LONGTEXT data in messages handled
- [ ] Timestamp precision maintained

**Notes**: _______________________________________________________________

---

## 🔗 Phase 6: Symfony Integration Testing (Optional)

### 6.1 API Compatibility
- [ ] Java application can parse Symfony API responses with new fields
- [ ] JSON deserialization works for new fields
- [ ] Date/time formatting matches
- [ ] Foreign key references resolved correctly

**Test endpoint**:
```bash
curl http://localhost:8000/api/utilisateurs/1 -H "Accept: application/json"
# Should include: face_descriptor, face_enabled, profile_picture, etc.
```

### 6.2 Data Synchronization
- [ ] Data created in Java appears in Symfony DB
- [ ] Data created in Symfony appears in Java
- [ ] No data corruption during sync
- [ ] Timestamps remain consistent

### 6.3 Bidirectional Operations
- [ ] Create in Java → Read in Symfony ✅
- [ ] Create in Symfony → Read in Java ✅
- [ ] Update in Java → See in Symfony ✅
- [ ] Update in Symfony → See in Java ✅

**Notes**: _______________________________________________________________

---

## 📊 Phase 7: Performance Validation

### 7.1 Query Performance
- [ ] No N+1 query problems
- [ ] Indexes being used correctly
- [ ] Query execution times acceptable (< 1s)

**Query analysis**:
```sql
EXPLAIN SELECT * FROM utilisateur WHERE id_user = 18;
EXPLAIN SELECT * FROM message WHERE receiver_id = 18;
```

### 7.2 Application Performance
- [ ] UI responsiveness maintained
- [ ] No memory leaks introduced
- [ ] Startup time acceptable
- [ ] Database operations don't timeout

### 7.3 Concurrent Access
- [ ] Multiple users can access data simultaneously
- [ ] No deadlock conditions
- [ ] Transaction isolation correct

**Notes**: _______________________________________________________________

---

## 🧹 Phase 8: Code Quality & Review

### 8.1 Code Style
- [ ] All code follows project conventions
- [ ] Consistent naming (camelCase for Java, snake_case for SQL)
- [ ] Proper javadoc comments
- [ ] No dead code or warnings

**Check**:
```bash
mvn clean compile -Dcheckstyle.enabled=true
```

### 8.2 Code Review
- [ ] All changes reviewed by team lead
- [ ] Security implications reviewed
- [ ] SQL injection protection verified
- [ ] No hardcoded credentials

### 8.3 Documentation
- [ ] Code comments updated
- [ ] README updated if needed
- [ ] Environment documentation updated
- [ ] API documentation updated

### 8.4 Logging & Monitoring
- [ ] Important operations logged
- [ ] Error conditions logged
- [ ] No excessive logging (performance)
- [ ] Log levels appropriate

**Notes**: _______________________________________________________________

---

## 🚀 Phase 9: Build & Deployment Preparation

### 9.1 Clean Build
- [ ] Full `mvn clean package` completes successfully
- [ ] No compiler warnings
- [ ] No runtime errors
- [ ] JAR/executable builds correctly

```bash
mvn clean package -DskipTests
```
✅ Build successful

### 9.2 Dependency Check
- [ ] All dependencies resolved
- [ ] No conflicting versions
- [ ] No security vulnerabilities in dependencies

```bash
mvn dependency:check
```

### 9.3 Documentation Artifacts
- [ ] Generated JavaDoc
- [ ] Build logs archived
- [ ] Version numbers updated
- [ ] Changelog recorded

### 9.4 Rollback Plan
- [ ] Database backup verified
- [ ] Original code backed up
- [ ] Rollback procedure documented
- [ ] Team understands rollback steps

**Rollback procedure**:
```bash
# If issues occur:
1. Restore database from backup
2. Revert to previous source branch
3. Rebuild and redeploy
```

**Notes**: _______________________________________________________________

---

## ✨ Phase 10: Production Deployment

### 10.1 Deployment Window
- [ ] Scheduled maintenance window confirmed
- [ ] Users notified
- [ ] Backup taken immediately before
- [ ] On-call team available

**Deployment time**: _______________  
**Maintenance window**: _______________  
**Estimated duration**: _______________

### 10.2 Deployment Steps
- [ ] Database migration executed in production
- [ ] Application redeployed
- [ ] Services restarted
- [ ] Health checks passed

### 10.3 Post-Deployment Verification
- [ ] Application accessible and responsive
- [ ] All key features working
- [ ] No errors in logs
- [ ] Performance metrics acceptable
- [ ] User reports no issues

### 10.4 Monitoring & Support
- [ ] Enhanced monitoring enabled
- [ ] Error tracking system active
- [ ] Support team on standby
- [ ] User communication ready

**Notes**: _______________________________________________________________

---

## 📋 Final Checklist

### All Phases Complete?
- [ ] Phase 1: Preparation ✅
- [ ] Phase 2: Database ✅
- [ ] Phase 3: Models ✅
- [ ] Phase 4: Services ✅
- [ ] Phase 5: Integration Testing ✅
- [ ] Phase 6: Symfony Integration (if applicable) ✅
- [ ] Phase 7: Performance ✅
- [ ] Phase 8: Code Quality ✅
- [ ] Phase 9: Build Prep ✅
- [ ] Phase 10: Deployment ✅

### Signoff
- [ ] Developer: _________________________ Signature: _________ Date: _______
- [ ] Code Reviewer: _____________________ Signature: _________ Date: _______
- [ ] QA Lead: ___________________________ Signature: _________ Date: _______
- [ ] Project Manager: ____________________ Signature: _________ Date: _______

---

## 📝 Issues & Resolutions

### Issues Encountered

**Issue #1**: 
- **Description**: _______________________________________________________________
- **Resolution**: _________________________________________________________________
- **Status**: ☐ Resolved ☐ Pending ☐ Escalated

**Issue #2**:
- **Description**: _______________________________________________________________  
- **Resolution**: _________________________________________________________________
- **Status**: ☐ Resolved ☐ Pending ☐ Escalated

**Issue #3**:
- **Description**: _______________________________________________________________
- **Resolution**: _________________________________________________________________
- **Status**: ☐ Resolved ☐ Pending ☐ Escalated

---

## 📊 Summary Statistics

- **Total Changes**: 5 models updated + 4 new models = 9 models
- **New Database Columns**: 11 columns added
- **New Database Tables**: 4 tables created
- **New Service Methods**: ~40+ methods created
- **Lines of Code Added**: ~2000+ LOC
- **Test Cases Added**: ~50+ tests
- **Documentation Pages**: 3 pages created

---

## 🎉 Completion Summary

**Project**: Smart Farm Java ↔ Symfony Database Alignment  
**Status**: ☐ In Progress ☐ COMPLETED  
**Date Completed**: _______________  
**Total Time Spent**: _______________ hours  
**Issues Resolved**: _______________  
**Open Issues**: _______________  

**Completion Notes**:
_______________________________________________________________________________
_______________________________________________________________________________
_______________________________________________________________________________

---

**Document Version**: 1.0  
**Last Updated**: May 5, 2026
**Next Review Date**: _______________


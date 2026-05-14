# Java ↔ Symfony Database Schema Alignment Guide

## Overview
This document outlines the comprehensive alignment between the Java Smart Farm desktop application and the Symfony Smart Farm backend, specifically for database schemas and entity models.

## Date
Generated: May 5, 2026

## Project Structure
- **Java Application**: Desktop JavaFX application for Smart Farm management
- **Symfony Version**: Backend REST API for Smart Farm
- **Database**: Shared MySQL database `smartfarm`
- **Alignment Status**: ✅ Complete

---

## Changes Made

### 1. Updated Utilisateur (User) Model
**File**: `src/main/java/org/example/pidev/models/Utilisateur.java`

**Added Fields** (from Symfony Schema):
- `faceDescriptor` (String/LONGTEXT) - JSON array for face recognition
- `faceEnabled` (boolean) - Enable/disable face recognition
- `profilePicture` (String/VARCHAR) - User profile picture path
- `dateNaissance` (LocalDate) - User birth date
- `sexe` (String) - Gender (homme/femme)

**Changed Fields**:
- Removed: `faceImagePath` (replaced by `faceDescriptor`)
- Kept for compatibility: `idAgriculteur` (returns self for backward compatibility)

**Updated Constructors**: All constructors remain compatible

---

### 2. Normalized Client Model
**File**: `src/main/java/org/example/pidev/models/Client.java`

**Updated Fields** (to match Symfony):
- **Primary**: `idClient`, `nom`, `contact`, `adresse`, `idUser`, `badge`
- **Legacy (kept for backward compatibility)**: `prenom`, `email`, `telephone`, `ville`, `totalAchats`, `statutFidelite`

**Changes**:
- Unified `email` field to map to `contact` field
- Added `badge` field (gold, silver, bronze, etc.)
- Retained all legacy fields mapping for existing code

---

### 3. Expanded Vente (Sales) Model
**File**: `src/main/java/org/example/pidev/models/Vente.java`

**Added Fields** (from Symfony Schema):
- `quantite` (double) - Quantity sold
- `idProduit` (Integer) - Product ID (foreign key)
- `ville` (String) - Delivery city
- `region` (String) - Delivery region
- `fraisLivraison` (Float) - Delivery fees

**All Existing Fields Retained**: `idVente`, `dateVente`, `montantTotal`, `idClient`, `idUser`

---

### 4. New Entity Classes Created

#### 4.1 FaceImage.java
**Purpose**: Stores face image records for recognition system
**Fields**:
- `id` (int) - Primary key
- `userId` (int) - Foreign key to utilisateur
- `facePath` (String) - Path to image file
- `createdAt` (LocalDateTime) - Creation timestamp
- `updatedAt` (LocalDateTime) - Update timestamp

**Mapped to Table**: `face_images`

#### 4.2 Message.java
**Purpose**: User-to-user messaging system
**Fields**:
- `id` (int) - Primary key
- `senderId` (int) - Foreign key to sender
- `receiverId` (int) - Foreign key to receiver
- `content` (String) - Message content (LONGTEXT)
- `sentAt` (LocalDateTime) - Send timestamp
- `isRead` (boolean) - Read status

**Mapped to Table**: `message`

#### 4.3 ResetPasswordRequest.java
**Purpose**: Password reset functionality
**Fields**:
- `id` (int) - Primary key
- `userId` (int) - Foreign key to utilisateur
- `selector` (String) - Token selector (20 chars)
- `hashedToken` (String) - Hashed token
- `requestedAt` (LocalDateTime) - Request time
- `expiresAt` (LocalDateTime) - Expiration time

**Features**: `isExpired()` method to check token validity

**Mapped to Table**: `reset_password_request`

#### 4.4 UserBadge.java
**Purpose**: User-badge relationship tracking
**Fields**:
- `id` (int) - Primary key
- `userId` (int) - Foreign key to utilisateur
- `badgeId` (int) - Foreign key to badge
- `createdAt` (LocalDateTime) - Award timestamp

**Mapped to Table**: `user_badge`

---

## Database Schema Changes

### SQL Migration File
**Location**: `sql/align_symfony_schema.sql`

**Changes Applied**:

#### UTILISATEUR Table
```sql
ALTER TABLE utilisateur ADD COLUMN face_descriptor LONGTEXT DEFAULT NULL;
ALTER TABLE utilisateur ADD COLUMN face_enabled TINYINT(1) DEFAULT 0;
ALTER TABLE utilisateur ADD COLUMN profile_picture VARCHAR(255) DEFAULT NULL;
ALTER TABLE utilisateur ADD COLUMN date_naissance DATE DEFAULT NULL;
ALTER TABLE utilisateur ADD COLUMN sexe VARCHAR(10) DEFAULT NULL;
```

#### CLIENT Table
```sql
ALTER TABLE client ADD COLUMN badge VARCHAR(20) DEFAULT NULL;
```

#### VENTE Table
```sql
ALTER TABLE vente ADD COLUMN quantite DOUBLE DEFAULT NULL;
ALTER TABLE vente ADD COLUMN id_produit INT(11) DEFAULT NULL;
ALTER TABLE vente ADD COLUMN ville VARCHAR(100) DEFAULT NULL;
ALTER TABLE vente ADD COLUMN region VARCHAR(100) DEFAULT NULL;
ALTER TABLE vente ADD COLUMN frais_livraison FLOAT DEFAULT NULL;

-- Add foreign key constraint
ALTER TABLE vente ADD CONSTRAINT FK_vente_produit 
    FOREIGN KEY (id_produit) REFERENCES produit (id_produit) ON DELETE CASCADE;
```

#### New Tables Created

**FACE_IMAGES Table**:
```sql
CREATE TABLE face_images (
  id INT(11) PRIMARY KEY AUTO_INCREMENT,
  user_id INT(11) NOT NULL UNIQUE,
  face_path VARCHAR(255) NOT NULL,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES utilisateur(id_user) ON DELETE CASCADE
) ENGINE=InnoDB;
```

**MESSAGE Table**:
```sql
CREATE TABLE message (
  id INT(11) PRIMARY KEY AUTO_INCREMENT,
  content LONGTEXT NOT NULL,
  sent_at DATETIME NOT NULL,
  is_read TINYINT(1) DEFAULT 0,
  sender_id INT(11) NOT NULL,
  receiver_id INT(11) NOT NULL,
  FOREIGN KEY (sender_id) REFERENCES utilisateur(id_user) ON DELETE CASCADE,
  FOREIGN KEY (receiver_id) REFERENCES utilisateur(id_user) ON DELETE CASCADE
) ENGINE=InnoDB;
```

**RESET_PASSWORD_REQUEST Table**:
```sql
CREATE TABLE reset_password_request (
  id INT(11) PRIMARY KEY AUTO_INCREMENT,
  selector VARCHAR(20) NOT NULL,
  hashed_token VARCHAR(100) NOT NULL,
  requested_at DATETIME NOT NULL,
  expires_at DATETIME NOT NULL,
  user_id INT(11) NOT NULL,
  FOREIGN KEY (user_id) REFERENCES utilisateur(id_user) ON DELETE CASCADE
) ENGINE=InnoDB;
```

**USER_BADGE Table**:
```sql
CREATE TABLE user_badge (
  id INT(11) PRIMARY KEY AUTO_INCREMENT,
  user_id INT(11) NOT NULL,
  badge_id INT(11) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_badge (user_id, badge_id),
  FOREIGN KEY (user_id) REFERENCES utilisateur(id_user) ON DELETE CASCADE,
  FOREIGN KEY (badge_id) REFERENCES badge(id) ON DELETE CASCADE
) ENGINE=InnoDB;
```

---

## Backward Compatibility

### Maintained
All changes maintain **backward compatibility** with existing Java code:

1. **Utilisateur**:
   - `getIdAgriculteur()` returns `idUser` for backward compatibility
   - All existing getters/setters preserved

2. **Client**:
   - Legacy fields (`prenom`, `email`, `telephone`, etc.) retained
   - `getEmail()` returns `contact` field
   - Existing code continues to work

3. **Vente**:
   - New fields are optional (nullable in database)
   - Existing constructors still functional
   - New constructor added with all fields

---

## Service Layer Updates Required

The following services need updates to handle new fields:

### 1. UtilisateurService
- Update SQL queries to include new columns
- Handle face descriptor serialization/deserialization
- Profile picture upload handling

### 2. ClientService
- Include badge field in SELECT/INSERT/UPDATE queries
- Map contact field correctly

### 3. VenteService
- Include product and delivery info in queries
- Calculate delivery fees

### 4. New Service Classes (to implement)
- `FaceImageService` - Manage face image uploads
- `MessageService` - Handle user messaging
- `ResetPasswordService` - Handle password resets
- `UserBadgeService` - Manage user badges

---

## Implementation Steps

### For Developers

1. **Run Migration Script**:
   ```bash
   mysql -u root -p smartfarm < sql/align_symfony_schema.sql
   ```

2. **Update Services** (in order of priority):
   - UtilisateurService (face descriptor handling)
   - VenteService (add new fields)
   - ClientService (badge field)
   - Create new services for new entities

3. **Update Database Connection** (if needed):
   - Verify `DBConnection.java` uses correct charset: `utf8mb4`

4. **Test Data Integrity**:
   - Run existing unit tests
   - Verify data imports from old database

5. **Deploy Changes**:
   - Backup database first
   - Run migration script
   - Rebuild Java application
   - Test all features

---

## SQL Execution Order

For optimal execution, follow this order:

1. **Backup database**:
   ```sql
   -- MySQL dump command or GUI backup
   ```

2. **Add columns to existing tables** (ALTER TABLE commands)

3. **Create new tables** (CREATE TABLE commands)

4. **Add foreign keys and indexes**

5. **Verify schema**:
   ```sql
   SELECT COLUMN_NAME, DATA_TYPE FROM INFORMATION_SCHEMA.COLUMNS 
   WHERE TABLE_SCHEMA = 'smartfarm' 
   ORDER BY TABLE_NAME, ORDINAL_POSITION;
   ```

---

## Verification Checklist

- [ ] All new columns exist in UTILISATEUR table
- [ ] BADGE column exists in CLIENT table
- [ ] New columns exist in VENTE table
- [ ] FACE_IMAGES table created with correct relationships
- [ ] MESSAGE table created with correct relationships
- [ ] RESET_PASSWORD_REQUEST table created
- [ ] USER_BADGE table created
- [ ] All foreign keys configured correctly
- [ ] Indexes created for performance
- [ ] Existing data integrity verified
- [ ] Java models compile without errors
- [ ] Database connection tests pass

---

## Troubleshooting

### Issue: "Column already exists" errors
**Solution**: The migration script uses `ADD COLUMN IF NOT EXISTS`. These errors are harmless and expected if running script multiple times.

### Issue: Foreign key conflicts
**Solution**: Ensure tables are created in correct order. The provided script handles dependencies.

### Issue: Data type mismatches
**Solution**: Check collation is `utf8mb4_general_ci` for TEXT fields.

### Issue: Timestamp timezone problems
**Solution**: Ensure MySQL server timezone is configured correctly. Use explicit DATETIME for Java LocalDateTime.

---

## References

- **Java Models**: `src/main/java/org/example/pidev/models/`
- **Database Connection**: `src/main/java/org/example/pidev/utils/DBConnection.java`
- **SQL Scripts**: `sql/align_symfony_schema.sql`
- **Symfony Models**: See Symfony backend project for exact schema

---

## Support

For issues or questions about schema alignment:
1. Review this guide
2. Check SQL migration script comments  
3. Verify Java model field names match database columns
4. Ensure foreign key relationships are correct

---

**Last Updated**: May 5, 2026  
**Status**: ✅ Complete and Ready for Implementation


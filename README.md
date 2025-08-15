# Software Development Agreement

**Date:** August 15, 2025  
**Parties:**  
- **Client:** [Client Name], [Client Address] 
- **Developer:** [Developer Name], [Developer Address]

The Client and Developer may each be referred to as a **"Party"** and collectively as the **"Parties"**.

---

## 1. Definitions

**1.1 Acceptance Tests**  
Tests conducted pursuant to Section 6 to determine whether the Deliverables meet the Specifications.

**1.2 Confidential Information**  
Any information disclosed by one Party to the other, whether in writing, orally, or by inspection, that is designated as "Confidential," "Proprietary," or similar.

**1.3 Deliverables**  
The software, documentation, and any other items to be delivered under this Agreement, as specified in **Schedule A**.

**1.4 Intellectual Property Rights**  
All worldwide rights under patent, copyright, trade secret, trademark, moral rights, and other similar laws.

**1.5 Services**  
The design, development, and delivery of software solutions and other services described in **Schedule A**.

**1.6 Specifications**  
The functional and technical requirements for the Deliverables as set forth in **Schedule A**.

---

## 2. Scope of Work

**2.1 Services Provided**  
Developer shall perform the Services and deliver the Deliverables per the Specifications.

**2.2 Modifications**  
Changes must be in writing and may require adjustments to fees, schedules, or both.

**2.3 Subcontracting**  
Developer may not subcontract any portion of the Services without prior written consent from Client.

---

## 3. Project Management and Communication

**3.1 Project Managers**  
Each Party shall appoint a project manager as the primary contact.

**3.2 Status Reports**  
Developer will provide weekly progress reports, including issues and upcoming tasks.

**3.3 Meetings**  
Regular project meetings (in-person or via teleconference) will be held to review progress and issues.

---

## 4. Term and Termination

**4.1 Term**  
Effective on the date above and continues until completion, unless terminated earlier.

**4.2 Termination for Convenience**  
Either Party may terminate for any reason with **30 days' written notice**.

**4.3 Termination for Cause**  
Immediate termination if a material breach is not cured within **15 days** of notice.

**4.4 Effect of Termination**  
Developer ceases work, delivers completed work, and is paid for work performed up to termination.

**4.5 Survival**  
Sections 4.4, 5–11 survive termination or expiration.

---

## 5. Payment Terms

**5.1 Fees**  
As set forth in **Schedule B**.

**5.2 Expenses**  
Client reimburses reasonable, pre-approved expenses.

**5.3 Payment Schedule**  
Per **Schedule B**. Late payments incur [Percentage]% monthly penalty.

**5.4 Invoices**  
Monthly invoicing; payment due within **30 days**.

**5.5 Taxes**  
Client is responsible for all applicable taxes.

---

## 6. Acceptance Testing

**6.1 Testing Procedures**  
Client has **[Number] days** to conduct Acceptance Tests.

**6.2 Acceptance Criteria**  
Per **Schedule A**.

**6.3 Notice of Acceptance or Rejection**  
Written notice with reasons for rejection.

**6.4 Correction of Defects**  
Developer corrects defects and resubmits; Client has another **[Number] days** for retesting.

---

## 7. Ownership and Intellectual Property

**7.1 Ownership**  
All IP in Deliverables transfers to Client upon full payment.

**7.2 Licence to Pre-existing IP**  
Developer grants a perpetual, non-exclusive, worldwide licence for pre-existing IP used in Deliverables.

**7.3 Client Materials**  
Client grants Developer a limited licence to use provided materials for the Services.

**7.4 Third-Party Materials**  
Licensed per applicable third-party terms.

---

## 8. Confidentiality

**8.1 Confidential Information**  
Both Parties agree to keep disclosed information confidential.

**8.2 Use and Disclosure**  
Only for fulfilling Agreement obligations.

**8.3 Return of Confidential Information**  
Return or destroy upon termination.

**8.4 Residuals**  
Unaided memory retention is not restricted.

---

## 9. Data Privacy

**9.1 Compliance with Laws**  
Developer complies with GDPR, CCPA, and applicable laws.

**9.2 Data Security**  
Implement encryption, access controls, security audits.

**9.3 Data Breach**  
Notify Client within **[Number] hours**, provide detailed report.

**9.4 Data Processing Agreement**  
Executed if required by law.

---

## 10. Warranties and Representations

**10.1 Mutual Warranties**  
Both Parties have the legal right to enter into the Agreement.

**10.2 Developer's Warranties**  
- Services will be professional and industry-standard.  
- Deliverables will meet Specifications and be defect-free.  
- No infringement of third-party IP.

**10.3 Client's Warranties**  
- Has rights to provided materials.  
- Will provide timely feedback and approvals.

**10.4 Disclaimer**  
All other warranties disclaimed unless expressly stated.

---

## 11. Liability and Indemnification

**11.1 Limitation of Liability**  
No indirect, incidental, consequential, or punitive damages (except for certain breaches).

**11.2 Cap on Liability**  
Developer’s liability capped at total amount paid by Client.

**11.3 Indemnification by Developer**  
Developer will defend Client against claims from breaches, negligence, or IP infringement.

**11.4 Indemnification by Client**  
Client will defend Developer against claims from breaches or negligence.

---

## 12. General Provisions

**12.1 Governing Law**  
This Agreement is governed by the laws of the Socialist Republic of Vietnam.

**12.2 Dispute Resolution**  
Disputes shall first be resolved by negotiation. If not settled within 30 days, they will be referred to the Vietnam International Arbitration Centre (VIAC) in Ho Chi Minh City. The arbitral award is final and binding.

**12.3 Entire Agreement**  
Supersedes all prior agreements.

**12.4 Amendments**  
Must be in writing and signed.

**12.5 Severability**  
Invalid provisions do not affect the rest.

**12.6 Waiver**  
No waiver of subsequent breaches.

**12.7 Assignment**  
No assignment without consent.

**12.8 Notices**  
Delivered in person, by email, or registered mail.

**12.9 Force Majeure**  
No liability for delays beyond reasonable control.

**12.10 Independent Contractors**  
No employment, partnership, or joint venture relationship created.

---

## Signatures

**Client:**  
By: ___________________________  
Name: _________________________  
Title: _________________________  

**Developer:**  
By: ___________________________  
Name: _________________________  
Title: _________________________  


---

## Schedule A – Scope of Work
### **Project Title:** Friends Management API

### 1. Objective
To design, develop, and deliver a RESTful API that enables management of friend connections, subscriptions, blocks, and update notifications between users identified by email addresses.

### 2. Scope of Work
Developer will implement the following API features:

1. **Create a friend connection between two email addresses.**
   - Description: Allow User A to add User B as a friend.  

2. **Retrieve the friends list for an email address.**
   - Description: Return all friends of a given user.  

3. **Retrieve common friends list between two email addresses.**  
   - Description: Return the mutual friends of User A and User B.  

4. **Subscribe to updates from another email address.**  
   - Description: User A follows User B without becoming friends.  

5. **Block updates from another email address.**  
   - Description: Prevents A from receiving updates from B and from adding each other as friends.  

6. **Retrieve all update recipients for an email address.**  
   - Description: Return all recipients eligible to receive updates from a given sender. Includes friends, followers, and mentioned users not blocking the sender.

### 3. Technical Specifications
- **Architecture:** RESTful API.
- **Data Format:** JSON for all requests and responses.
- **Persistence:** Relational database (PostgreSQL).
- **Frameworks:** Java/Spring Boot.
- **Error Handling:** Standard HTTP status codes and error messages.
- **Security:** Input validation, prevention of duplicate connections, and blocking logic enforcement.

### 4. Milestones & Time Frames

| Milestone | Description | Target Date |
|-----------|-------------|-------------|
| 1 | Setup & Planning | Week 1 |
| 2 | Friend connection, Friends list, Common friends | Week 2 |
| 3 | Subscription and Blocking | Week 3 |
| 4 | Update Recipients and Mentions | Week 4 |
| 5 | Testing, Deployment, Documentation | Week 5 |

> Dates are indicative and subject to adjustment upon mutual agreement.

### 5. Database Design
The system will use a relational database with the following core tables:  
- **Account** (stores unique user email addresses)  
- **Friend** (friendship relationships)  
- **Follow** (follow relationships)  
- **Block** (blocking relationships)  

**Database ER Diagram**  
<img src="assets/db.png" style="width:75%;"/>

---

## Schedule B – Payment Schedule
**Hourly Rate:**  
$80/hour — billed monthly based on approved timesheets.  

**Estimated Hours:**  
200 hours over the course of the project.  

**Additional Expenses (pre-approved by Client):**  
- Third-party software licenses.  
- Domain registration and hosting fees (if applicable).  
- Travel expenses for on-site meetings.  

**Payment Terms:**  
- All invoices are payable within **30 days** of receipt.  
- Late payments incur **1.5% interest per month** on the outstanding balance.  

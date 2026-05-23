# 🅿️ Parking Lot System — LLD

A clean Low-Level Design implementation of a Parking Lot system in Java, built to demonstrate Object-Oriented Design principles, GoF Design Patterns, and thread safety — commonly asked in SDE interviews.

---

## 📐 Design Patterns Used

| Pattern | Where Applied | Why |
|---|---|---|
| **Singleton** | `ParkingLot` | Only one parking lot instance should exist system-wide |
| **Strategy** | `PricingStrategy` | Swap pricing logic (hourly, flat rate, weekend) without changing core code |
| **Abstract Class** | `Vehicle` | Extend to `Bike`, `Car`, `Truck` without modifying existing logic |

---

## 🗂️ Class Structure

```
ParkingLot (Singleton)
├── List<Level>
│   └── Level
│       └── List<ParkingSlot>
│           ├── SlotType (SMALL / MEDIUM / LARGE)
│           ├── isOccupied: boolean
│           └── Vehicle
│
├── Vehicle (abstract)
│   ├── Bike    → VehicleType.BIKE  → SlotType.SMALL
│   ├── Car     → VehicleType.CAR   → SlotType.MEDIUM
│   └── Truck   → VehicleType.TRUCK → SlotType.LARGE
│
├── Ticket
│   ├── ticketId: UUID
│   ├── vehicle: Vehicle
│   ├── slot: ParkingSlot
│   └── entryTime: LocalDateTime
│
├── EntryGate   → park(Vehicle)  → returns Ticket
├── ExitGate    → exit(Ticket)   → returns fee (double)
│
└── PricingStrategy (interface)
    └── HourlyPricing implements PricingStrategy
```

---

## 📁 File Breakdown

### `VehicleType.java` + `SlotType.java`
Two **separate** enums — vehicle types and slot types are independent concerns.
- Adding a new vehicle type doesn't force a slot type change.
- `SlotType`: `SMALL`, `MEDIUM`, `LARGE`
- `VehicleType`: `BIKE`, `CAR`, `TRUCK`

---

### `Vehicle.java` (Abstract Class)
Base class for all vehicles. Never instantiated directly.

```java
public abstract class Vehicle {
    private String licensePlate;
    private VehicleType vehicleType;
    // constructor + getters
}
```

**Why abstract?** Open/Closed Principle — add `Van`, `Bus` by extending, not modifying.

Concrete subclasses: `Bike.java`, `Car.java` — each passes its `VehicleType` to super.

---

### `ParkingSlot.java`
Represents one physical slot. Knows its own type and whether it's occupied.

**Key method:**
```java
public boolean canFitVehicle(VehicleType vehicleType) {
    return !isOccupied && slotType == getRequiredSlotType(vehicleType);
}
```

**Why here?** Single Responsibility — the slot decides if a vehicle fits. Level/ParkingLot shouldn't have this logic.

---

### `Level.java`
Represents one floor of the parking lot. Contains a list of `ParkingSlot`.

**Key method:**
```java
public synchronized ParkingSlot getAvailableSlot(VehicleType vehicleType) {
    return slots.stream()
        .filter(slot -> slot.canFitVehicle(vehicleType))
        .findFirst()
        .orElse(null);
}
```

**Why `synchronized`?** Thread safety — two cars entering simultaneously could get the same slot without it.

---

### `PricingStrategy.java` (Interface) + `HourlyPricing.java`
Strategy Pattern for pricing. `ExitGate` accepts any implementation.

```java
public interface PricingStrategy {
    double calculateFee(VehicleType vehicleType, long parkedMinutes);
}
```

**Why interface?** Tomorrow add `WeekendPricing`, `MemberPricing` — zero changes to existing code. Open/Closed Principle.

Pricing in `HourlyPricing`:
- `BIKE` → ₹10/hour
- `CAR` → ₹20/hour
- `TRUCK` → ₹40/hour
- Minimum 1 hour charged (ceiling applied).

---

### `Ticket.java`
Generated at entry. Carries all info needed for exit calculation.

```java
private String ticketId;      // UUID
private Vehicle vehicle;
private ParkingSlot slot;
private LocalDateTime entryTime;  // NOT java.sql.Time — business logic uses LocalDateTime
```

---

### `ParkingLot.java` (Singleton)
The brain of the system. Single instance, thread-safe via double-checked locking.

```java
public static ParkingLot getInstance() {
    if (instance == null) {
        synchronized (ParkingLot.class) {
            if (instance == null) instance = new ParkingLot();
        }
    }
    return instance;
}
```

**`park(Vehicle)`** → loops through levels, finds first available slot, assigns vehicle, returns Ticket.  
**`exit(Ticket, PricingStrategy)`** → calculates time diff, releases slot, returns fee.

**Why Singleton?** Multiple instances = inconsistent slot counts. One parking lot = one instance.

---

### `Main.java`
Clean driver. Just setup + simulate entry/exit. No business logic here.

```java
ParkingLot lot = ParkingLot.getInstance();
lot.addLevel(new Level(1, 10, 20, 5)); // 10 small, 20 medium, 5 large slots

Vehicle car = new Car("KA-01-AB-1234");
Ticket ticket = lot.park(car);

double fee = lot.exit(ticket, new HourlyPricing());
System.out.println("Fee: ₹" + fee);
```

---

## 🔴 Common Mistakes (What NOT to do)

These were in the first version — documented here for revision.

| Mistake | Problem | Fix Applied |
|---|---|---|
| `CarDetails[4][2][10]` hardcoded 3D array | Magic numbers, no scalability, can't add floors/types | `Level` + `ParkingSlot` classes |
| `AssignedSlot` doing everything | God class, violates SRP | Split into `Level`, `ParkingSlot`, `ParkingLot` |
| No vehicle-to-slot type mapping | Bike and car in same slot | `canFitVehicle()` in `ParkingSlot` |
| Pricing hardcoded in Checkout | Can't change pricing without changing class | `PricingStrategy` interface |
| No `synchronized` anywhere | Race condition on concurrent entries | `synchronized` in `getAvailableSlot()` |
| `java.sql.Time` for entry/exit | Wrong class — sql.Time is for DB, not business logic | `LocalDateTime` |
| State (HashMap) living in `Main` | Core state should live in the system, not the driver | State moved into `ParkingLot` |
| No interfaces | Can't extend without modifying | `Vehicle` abstract + `PricingStrategy` interface |
| No null handling when lot is full | Silent failure | `RuntimeException` thrown with clear message |

---

## ✅ SOLID Principles Applied

| Principle | Applied Where |
|---|---|
| **S**ingle Responsibility | `ParkingSlot` handles only slot state. `Level` handles slot collection. `ParkingLot` orchestrates. |
| **O**pen/Closed | Add new `VehicleType` or `PricingStrategy` without modifying existing classes |
| **L**iskov Substitution | `Bike`, `Car`, `Truck` all substitutable wherever `Vehicle` is expected |
| **I**nterface Segregation | `PricingStrategy` is a focused single-method interface |
| **D**ependency Inversion | `ParkingLot.exit()` depends on `PricingStrategy` interface, not `HourlyPricing` directly |

---

## 🔒 Thread Safety

| Where | Mechanism | Why |
|---|---|---|
| `Level.getAvailableSlot()` | `synchronized` method | Prevents two cars getting the same slot simultaneously |
| `ParkingLot.getInstance()` | Double-checked locking | Thread-safe Singleton creation |

---

## 🚀 How to Extend

**Add a new vehicle type (e.g., Van):**
1. Add `VAN` to `VehicleType` enum
2. Add `EXTRA_LARGE` to `SlotType` enum (if needed)
3. Create `Van.java extends Vehicle`
4. Add case in `ParkingSlot.getRequiredSlotType()`
5. Add case in `HourlyPricing.calculateFee()`
6. Zero changes to `ParkingLot`, `Level`, `EntryGate`, `ExitGate` ✅

**Add a new pricing model (e.g., Weekend pricing):**
1. Create `WeekendPricing implements PricingStrategy`
2. Pass it to `lot.exit(ticket, new WeekendPricing())`
3. Zero changes to existing code ✅

---

## 📚 Interview Revision Checklist

Before an interview, make sure you can answer:

- [ ] Why Singleton for ParkingLot?
- [ ] Why `synchronized` in `getAvailableSlot()` and not elsewhere?
- [ ] Why Strategy pattern for pricing?
- [ ] What's the difference between abstract class and interface here? Why `Vehicle` is abstract, not an interface?
- [ ] What happens if parking lot is full? How does the exception propagate?
- [ ] How would you add multi-entry/multi-exit gates?
- [ ] How would you track revenue collected per day?
- [ ] How would you handle VIP/reserved slots?

---

## 🛠️ Tech Stack

- **Language:** Java 17+
- **Concurrency:** `synchronized`, double-checked locking
- **Time:** `java.time.LocalDateTime`, `java.time.temporal.ChronoUnit`
- **No frameworks** — pure OOP, interview-ready

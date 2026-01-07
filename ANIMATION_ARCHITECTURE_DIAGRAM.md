# Animation Flow Diagram

## Scroll Behavior Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    ScrollBehaviorState                       │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  • scrollOffset: Float                                 │ │
│  │  • isVisible: Boolean                                  │ │
│  │  • nestedScrollConnection: NestedScrollConnection      │ │
│  │  • reset()                                             │ │
│  │  • animatedVisibilityFraction()                        │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ observes
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                        MainScreen                            │
│  ┌────────────────────────────────────────────────────────┐ │
│  │  Box(modifier = Modifier.nestedScroll(...))           │ │
│  │    │                                                   │ │
│  │    ├─► Column(offset = headerOffset)                  │ │
│  │    │     ├─ ValguardHeader                            │ │
│  │    │     └─ TabNavigation                             │ │
│  │    │                                                   │ │
│  │    ├─► LazyColumn (scrollable content)                │ │
│  │    │     └─ Coin items / Portfolio items              │ │
│  │    │                                                   │ │
│  │    └─► Box(offset = bottomNavOffset)                  │ │
│  │          └─ CryptoBottomNavigation                    │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## Animation State Flow

```
┌──────────────┐
│ User Scrolls │
└──────┬───────┘
       │
       ▼
┌─────────────────────────┐
│ NestedScrollConnection  │
│   onPreScroll()         │
└──────┬──────────────────┘
       │
       ▼
┌─────────────────────────┐     YES     ┌──────────────────┐
│ Delta < -5 (scroll down)├────────────►│ isVisible = false│
└─────────────────────────┘             └────────┬─────────┘
       │                                          │
       │ NO                                       │
       ▼                                          │
┌─────────────────────────┐     YES              │
│ Delta > 5 (scroll up)   ├──────────┐           │
└─────────────────────────┘          │           │
                                     ▼           │
                            ┌──────────────────┐ │
                            │ isVisible = true │ │
                            └────────┬─────────┘ │
                                     │           │
                                     └───────────┘
                                             │
                                             ▼
                        ┌──────────────────────────────────┐
                        │ animatedVisibilityFraction()     │
                        │   Returns: 0f (hidden) → 1f      │
                        └────────┬─────────────────────────┘
                                 │
                                 ▼
                    ┌────────────────────────────┐
                    │  animateDpAsState()        │
                    │  • headerOffset            │
                    │    -100dp ↔ 0dp           │
                    │  • bottomNavOffset         │
                    │    0dp ↔ 100dp            │
                    └────────┬───────────────────┘
                             │
                             ▼
                  ┌──────────────────────┐
                  │  UI Animates Smoothly │
                  │  Spring: ~350ms       │
                  │  60 FPS Performance   │
                  └───────────────────────┘
```

## Threshold Logic

```
Scroll Down Detection:
─────────────────────
scrollOffset changes → delta < -5px
          ↓
threshold exceeded (50px from last position)
          ↓
isVisible = false
          ↓
Animate OUT


Scroll Up Detection:
───────────────────
scrollOffset changes → delta > 5px
          ↓
threshold exceeded (25px from last position)
          ↓
isVisible = true
          ↓
Animate IN
```

## Component Offset Mapping

```
VISIBLE STATE (isVisible = true):
┌─────────────────────────┐
│  Header (offset: 0dp)   │ ← Fully visible
├─────────────────────────┤
│                         │
│     Content Area        │
│                         │
├─────────────────────────┤
│ Bottom Nav (offset: 0)  │ ← Fully visible
└─────────────────────────┘


HIDDEN STATE (isVisible = false):
                              ← Header (offset: -200dp) COMPLETELY ABOVE screen
┌─────────────────────────┐
│                         │
│     Content Area        │
│     (Full screen!)      │
│                         │
└─────────────────────────┘
                              ← Bottom Nav (offset: 120dp) COMPLETELY BELOW screen
```

## Animation Timing

```
Timeline: Scroll Down
─────────────────────
0ms     User scrolls
        ↓
50ms    Threshold detected
        ↓
100ms   Animation starts
        │ Header: 0dp → -100dp
        │ Bottom: 0dp → 100dp
        ↓
450ms   Animation complete
        ↓
Result: Maximum content space


Timeline: Scroll Up
──────────────────
0ms     User scrolls up
        ↓
25ms    Threshold detected
        ↓
75ms    Animation starts
        │ Header: -100dp → 0dp
        │ Bottom: 100dp → 0dp
        ↓
425ms   Animation complete
        ↓
Result: Full navigation restored
```

## Spring Animation Curve

```
Value
  1.0 ┤                          ╭────────
      │                     ╭────╯
  0.8 ┤                ╭────╯
      │           ╭────╯
  0.6 ┤      ╭────╯
      │  ╭───╯
  0.4 ┤──╯
      │
  0.2 ┤
      │
  0.0 ┤
      └────────────────────────────────► Time
      0ms    100ms   200ms   300ms  400ms

Spring Properties:
• dampingRatio = NoBouncy (smooth stop)
• stiffness = Medium (natural speed)
• No overshoot or oscillation
```

## State Management

```
Screen Lifecycle:
─────────────────

onCreate/onStart
     ↓
ScrollBehaviorState created
     ↓
isVisible = true (default)
     ↓
User interacts
     ↓
State updates based on scroll
     ↓
Screen switch detected
     ↓
reset() called
     ↓
isVisible = true (restored)
```

## Performance Optimization

```
Optimization Strategy:
─────────────────────

1. Threshold Gating
   └─ Prevents micro-updates
      └─ Less recomposition

2. GPU Acceleration
   └─ offset() modifier
      └─ Hardware accelerated

3. Spring Animation
   └─ Native Compose animation
      └─ Optimized rendering

4. State Memoization
   └─ remember { }
      └─ Cached across recompositions

5. Smart Composition
   └─ Only visibility changes trigger
      └─ Minimal recomposition scope

Result: 60 FPS on all devices
```


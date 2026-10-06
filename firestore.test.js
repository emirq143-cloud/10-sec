const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// --- SECURITY BOUNDS TESTS ---

test("Unauthenticated user: cannot read leaderboard", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("leaderboard").get());
});

test("Unauthenticated user: cannot write leaderboard entry", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("leaderboard").doc(ALICE_UID).set({
    userId: ALICE_UID,
    userName: "Alice",
    avatarId: "default",
    avatarEmoji: "🧑‍🚀",
    brainScore: 1250,
    level: 5,
    city: "İstanbul",
    updatedAt: new Date(),
  }));
});

test("Authenticated user: can write their own valid leaderboard entry", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("leaderboard").doc(ALICE_UID).set({
    userId: ALICE_UID,
    userName: "Alice",
    avatarId: "default",
    avatarEmoji: "🧑‍🚀",
    brainScore: 1250,
    level: 5,
    city: "İstanbul",
    updatedAt: new Date(),
  }));
});

test("Authenticated user: cannot write another user's leaderboard entry", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("leaderboard").doc(BOB_UID).set({
    userId: BOB_UID,
    userName: "Bob",
    avatarId: "default",
    avatarEmoji: "🧑‍🚀",
    brainScore: 999,
    level: 3,
    city: "Ankara",
    updatedAt: new Date(),
  }));
});

test("Authenticated user: cannot write invalid entry (missing fields or negative score)", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("leaderboard").doc(ALICE_UID).set({
    userId: ALICE_UID,
    userName: "Alice",
    avatarId: "default",
    avatarEmoji: "🧑‍🚀",
    brainScore: -50,
    level: 5,
    city: "İstanbul",
    updatedAt: new Date(),
  }));
});

test("Authenticated user: can read leaderboard entries", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("leaderboard").doc(BOB_UID).set({
      userId: BOB_UID,
      userName: "Bob",
      avatarId: "default",
      avatarEmoji: "🧑‍🚀",
      brainScore: 2000,
      level: 6,
      city: "İzmir",
      updatedAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(aliceDb.collection("leaderboard").get());
});

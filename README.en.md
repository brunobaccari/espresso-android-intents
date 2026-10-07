# Espresso — Activity integration and intents

[Versão em português](README.md)

Five Java tests against Android's official [IntentsBasicSample](https://github.com/android/testing-samples/tree/8c9df3a534ef99e44d481d96c00a5fc1970f7c70/ui/espresso/IntentsBasicSample), using Espresso, Espresso Intents and ActivityScenario. The app is fetched at a pinned commit; only the instrumentation source set changes.

## Scenarios and integration boundaries

- The typed number reaches `ACTION_CALL` unchanged.
- The sample's real contact Activity returns its demo number.
- A selected contact replaces existing input.
- Cancelling selection preserves typed input.
- Recreating the Activity preserves the unsubmitted field.

Phone calls are always intercepted through Espresso Intents; no real call is made. Two cases stub contact selection results to exercise success/cancellation; another executes the real sample Activity. That Activity is a demo, not the device address book. These are not claims of real contacts or telephony integration.

## Run

Linux, Java 17, Python 3.13, Android SDK and an API 34 emulator. The Gradle Wrapper comes from the pinned official source.

```bash
cp .env.example .env
python -m pip install -r requirements.txt
python prepare_app.py
chmod +x .upstream/ui/espresso/IntentsBasicSample/gradlew
.upstream/ui/espresso/IntentsBasicSample/gradlew -p .upstream/ui/espresso/IntentsBasicSample connectedDebugAndroidTest
```

Preparation verifies the SHA and injects `src/androidTest/java/ContactIntentsTest.java` into an isolated source set without changing app behavior. Downloaded source, builds and reports are ignored. Upstream tests are not counted as portfolio-authored scenarios.

## Results and triage

[Actions](https://github.com/brunobaccari/espresso-android-intents/actions) builds both APKs, runs the five tests and publishes a per-case summary. `android-results` retains JUnit XML and instrumentation HTML for 14 days, including available outputs after failures.

Skipped cases, a count different from five, missing reports or test failures block the gate. Investigate intent/payload differences separately from installation/emulator errors. No sleeps, rerun-until-green or real contact dependency. Android only; no iOS, physical-device or customer-data claim.

The Actions summary lists every scenario, duration, totals and blocking reason. The gate requires the count configured in the workflow, with no failures or skips; missing or invalid JUnit fails the gate. The summary is also included in the artifact.

Husky: with Node 24 and the stack dependencies installed, run `npm ci` to enable pre-commit. `npm run check:local` checks the diff, report gate and existing type/lint checks. The hook also rejects ignored files in the index. Browser, emulator and API tests remain in CI.

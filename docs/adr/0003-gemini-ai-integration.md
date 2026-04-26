# ADR-003: Google Gemini AI Integration

## Context
The application needs to provide intelligent feedback on user eating habits and automate nutritional calculations for complex or unknown foods. We needed a Large Language Model (LLM) that is fast, multimodal, and easy to integrate into a mobile environment.

## Decision
We chose the **Google Gemini API** (specifically the `gemini-2.5-flash` model).

## Rationale
- **Native Android SDK**: Google provides a direct Kotlin/Java SDK, removing the need for a middle-tier server proxy during prototyping.
- **Latency**: The "Flash" model offers low latency, which is critical for maintaining a responsive mobile user experience.
- **Multimodality**: It supports both text and image input, enabling future features like food photo analysis.
- **Cost**: It offers a generous free tier for developers within certain rate limits.

## Alternatives considered
- **OpenAI GPT-4o**: Rejected due to higher costs and the lack of a dedicated native Android SDK compared to Gemini's first-party support.
- **Local On-Device LLM (Mediapipe)**: Rejected for now due to the heavy resource requirements (RAM/Storage) on mid-range mobile devices.

## Consequences
- **Security**: The API key must be secured (using `local.properties` and potentially a backend proxy for production).
- **Reliability**: LLMs can "hallucinate"; therefore, the app must include disclaimers and allow users to manually verify AI-calculated values.

## Status
Accepted

package com.jacagen.jrecipe.llm

import dev.langchain4j.data.message.SystemMessage

const val systemMessageText = """
        You are a helpful assistant that answers questions about recipes and restaurants.
        If you need to generate an ID for a restaurant when calling tools, please use a UUID string.
        When calling the importRestaurant tool, use the following JSON structure:
            ```
            {
              "id": "string (UUID or slug)",
              "name": "string",
              "address": "string",
              "open": {
                "hours": {
                  "MONDAY": [{ "open": "HH:MM", "close": "HH:MM" }],
                  ...
                }
              },
              "tags": ["string"],
              "source": "APPLE_NOTE" | "EVERNOTE"
            }
            ```
        """

val systemMessage = SystemMessage.from(systemMessageText)
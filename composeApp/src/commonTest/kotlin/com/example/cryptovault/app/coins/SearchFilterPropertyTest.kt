package com.example.cryptovault.app.coins

import com.example.cryptovault.app.coins.presentation.CoinsState
import com.example.cryptovault.app.coins.presentation.UiCoinListItem
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchFilterPropertyTest {

    private fun generateRandomString(length: Int = Random.nextInt(3, 10)): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        return (1..length).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }

    private fun createCoin(
        id: String = generateRandomString(),
        name: String = generateRandomString(10),
        symbol: String = generateRandomString(4).uppercase()
    ) = UiCoinListItem(
        id = id,
        name = name,
        symbol = symbol,
        iconUrl = "https://example.com/$id.png",
        formattedPrice = "$${Random.nextDouble(0.01, 100000.0)}",
        formattedChange = "${Random.nextDouble(-50.0, 50.0)}%",
        isPositive = Random.nextBoolean()
    )

    private fun createCoinsState(
        coins: List<UiCoinListItem>,
        searchQuery: String = ""
    ) = CoinsState(
        isLoading = false,
        coins = coins,
        searchQuery = searchQuery
    )

    @Test
    fun `empty query returns all coins`() {
        repeat(100) {
            val coins = (1..Random.nextInt(1, 20)).map { createCoin() }
            val state = createCoinsState(coins, searchQuery = "")

            assertEquals(
                coins.size,
                state.filteredCoins.size,
                "Empty query should return all coins"
            )
        }
    }

    @Test
    fun `blank query returns all coins`() {
        repeat(100) {
            val coins = (1..Random.nextInt(1, 20)).map { createCoin() }
            val blankQueries = listOf("", " ", "  ", "\t", "\n", "   ")
            val query = blankQueries[Random.nextInt(blankQueries.size)]
            val state = createCoinsState(coins, searchQuery = query)

            assertEquals(
                coins.size,
                state.filteredCoins.size,
                "Blank query '$query' should return all coins"
            )
        }
    }

    @Test
    fun `filter by exact name match`() {
        repeat(100) {
            val targetName = generateRandomString(8)
            val targetCoin = createCoin(name = targetName)
            val otherCoins = (1..Random.nextInt(1, 10)).map { createCoin() }
            val allCoins = (otherCoins + targetCoin).shuffled()

            val state = createCoinsState(allCoins, searchQuery = targetName)

            assertTrue(
                state.filteredCoins.any { it.name == targetName },
                "Exact name match should be included"
            )
        }
    }

    @Test
    fun `filter by partial name match`() {
        repeat(100) {
            val fullName = "Bitcoin${generateRandomString(5)}"
            val targetCoin = createCoin(name = fullName)
            val otherCoins = (1..Random.nextInt(1, 10)).map { createCoin() }
            val allCoins = (otherCoins + targetCoin).shuffled()

            val state = createCoinsState(allCoins, searchQuery = "Bitcoin")

            assertTrue(
                state.filteredCoins.any { it.name == fullName },
                "Partial name match should be included"
            )
        }
    }

    @Test
    fun `filter by exact symbol match`() {
        repeat(100) {
            val targetSymbol = generateRandomString(4).uppercase()
            val targetCoin = createCoin(symbol = targetSymbol)
            val otherCoins = (1..Random.nextInt(1, 10)).map { createCoin() }
            val allCoins = (otherCoins + targetCoin).shuffled()

            val state = createCoinsState(allCoins, searchQuery = targetSymbol)

            assertTrue(
                state.filteredCoins.any { it.symbol == targetSymbol },
                "Exact symbol match should be included"
            )
        }
    }

    @Test
    fun `filter by partial symbol match`() {
        repeat(100) {
            val fullSymbol = "BTC${generateRandomString(2).uppercase()}"
            val targetCoin = createCoin(symbol = fullSymbol)
            val otherCoins = (1..Random.nextInt(1, 10)).map { createCoin() }
            val allCoins = (otherCoins + targetCoin).shuffled()

            val state = createCoinsState(allCoins, searchQuery = "BTC")

            assertTrue(
                state.filteredCoins.any { it.symbol == fullSymbol },
                "Partial symbol match should be included"
            )
        }
    }

    @Test
    fun `filter is case insensitive for name`() {
        repeat(100) {
            val targetName = "Bitcoin"
            val targetCoin = createCoin(name = targetName)
            val otherCoins = (1..Random.nextInt(1, 10)).map { createCoin() }
            val allCoins = (otherCoins + targetCoin).shuffled()

            val queries = listOf("bitcoin", "BITCOIN", "BiTcOiN", "bItCoIn")
            val query = queries[Random.nextInt(queries.size)]
            val state = createCoinsState(allCoins, searchQuery = query)

            assertTrue(
                state.filteredCoins.any { it.name == targetName },
                "Case-insensitive name search with '$query' should find '$targetName'"
            )
        }
    }

    @Test
    fun `filter is case insensitive for symbol`() {
        repeat(100) {
            val targetSymbol = "BTC"
            val targetCoin = createCoin(symbol = targetSymbol)
            val otherCoins = (1..Random.nextInt(1, 10)).map { createCoin() }
            val allCoins = (otherCoins + targetCoin).shuffled()

            val queries = listOf("btc", "BTC", "Btc", "bTc")
            val query = queries[Random.nextInt(queries.size)]
            val state = createCoinsState(allCoins, searchQuery = query)

            assertTrue(
                state.filteredCoins.any { it.symbol == targetSymbol },
                "Case-insensitive symbol search with '$query' should find '$targetSymbol'"
            )
        }
    }

    @Test
    fun `non-matching query returns empty list`() {
        repeat(100) {
            val coins = (1..Random.nextInt(1, 10)).map { createCoin() }
            val nonMatchingQuery = "ZZZZNONEXISTENT${Random.nextInt(10000)}"
            val state = createCoinsState(coins, searchQuery = nonMatchingQuery)

            assertTrue(
                state.filteredCoins.isEmpty(),
                "Non-matching query should return empty list"
            )
        }
    }

    @Test
    fun `all matching coins are included`() {
        repeat(50) {
            val prefix = "Test"
            val matchingCoins = (1..Random.nextInt(2, 5)).map { 
                createCoin(name = "$prefix${generateRandomString(5)}")
            }
            val nonMatchingCoins = (1..Random.nextInt(2, 5)).map { createCoin() }
            val allCoins = (matchingCoins + nonMatchingCoins).shuffled()

            val state = createCoinsState(allCoins, searchQuery = prefix)

            assertEquals(
                matchingCoins.size,
                state.filteredCoins.size,
                "All matching coins should be included"
            )
            matchingCoins.forEach { coin ->
                assertTrue(
                    state.filteredCoins.any { it.id == coin.id },
                    "Matching coin ${coin.name} should be in filtered results"
                )
            }
        }
    }

    @Test
    fun `only matching coins are included`() {
        repeat(50) {
            val matchingName = "UniqueMatch${Random.nextInt(10000)}"
            val matchingCoin = createCoin(name = matchingName)
            val nonMatchingCoins = (1..Random.nextInt(5, 10)).map { createCoin() }
            val allCoins = (nonMatchingCoins + matchingCoin).shuffled()

            val state = createCoinsState(allCoins, searchQuery = matchingName)

            assertEquals(
                1,
                state.filteredCoins.size,
                "Only matching coin should be included"
            )
            assertEquals(
                matchingCoin.id,
                state.filteredCoins.first().id,
                "The matching coin should be the one in results"
            )
        }
    }

    @Test
    fun `filter matches either name or symbol`() {
        repeat(100) {
            val searchTerm = "XYZ"
            val coinWithMatchingName = createCoin(name = "XYZCoin", symbol = "ABC")
            val coinWithMatchingSymbol = createCoin(name = "OtherCoin", symbol = "XYZ")
            val nonMatchingCoin = createCoin(name = "Different", symbol = "DIF")
            val allCoins = listOf(coinWithMatchingName, coinWithMatchingSymbol, nonMatchingCoin).shuffled()

            val state = createCoinsState(allCoins, searchQuery = searchTerm)

            assertEquals(
                2,
                state.filteredCoins.size,
                "Should match coins by name OR symbol"
            )
            assertTrue(
                state.filteredCoins.any { it.id == coinWithMatchingName.id },
                "Coin with matching name should be included"
            )
            assertTrue(
                state.filteredCoins.any { it.id == coinWithMatchingSymbol.id },
                "Coin with matching symbol should be included"
            )
        }
    }

    @Test
    fun `filter with special characters`() {
        repeat(50) {
            val coins = (1..5).map { createCoin() }
            val specialQueries = listOf(".", "*", "?", "[", "]", "(", ")", "+")
            val query = specialQueries[Random.nextInt(specialQueries.size)]
            val state = createCoinsState(coins, searchQuery = query)

            // Should not crash and should return results based on contains
            assertTrue(
                state.filteredCoins.size <= coins.size,
                "Filter with special character '$query' should not crash"
            )
        }
    }

    @Test
    fun `filter preserves coin order`() {
        repeat(50) {
            val prefix = "Match"
            val matchingCoins = (1..5).mapIndexed { index, _ ->
                createCoin(id = "coin_$index", name = "$prefix$index")
            }
            val state = createCoinsState(matchingCoins, searchQuery = prefix)

            matchingCoins.forEachIndexed { index, coin ->
                assertEquals(
                    coin.id,
                    state.filteredCoins[index].id,
                    "Filtered coins should preserve original order"
                )
            }
        }
    }

    @Test
    fun `filter with single character`() {
        repeat(100) {
            val char = ('a'..'z').random()
            val matchingCoin = createCoin(name = "${char}Coin")
            val nonMatchingCoin = createCoin(name = "Other")
            val allCoins = listOf(matchingCoin, nonMatchingCoin)

            val state = createCoinsState(allCoins, searchQuery = char.toString())

            assertTrue(
                state.filteredCoins.any { it.id == matchingCoin.id },
                "Single character search should work"
            )
        }
    }
}

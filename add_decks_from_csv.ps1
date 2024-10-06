


$decks = Import-Csv .\decks.csv
$owners = @{}
foreach ($deck in $decks) {
    if(($deck.Deck -ne "") -and ($deck.Deck -ne "Other")) {
        $owners["$($deck.Owner)"] = 0
    }
}
foreach ($owner in $($owners.Keys)) {
        $response = curl -X POST http://localhost:8080/api/users -H "Content-Type: application/json" -d "{""username"": ""$owner""}" | ConvertFrom-Json
        $owners[$owner] = $response.id
    }
foreach ($deck in $decks) {
        if(($deck.Deck -ne "") -and ($deck.Deck -ne "Other")) {
            curl -X POST http://localhost:8080/api/decks -H "Content-Type: application/json" -d "{""name"": ""$($deck.Deck)"", ""commander"": ""$($deck.Commander)"", ""ownerId"": $($owners[$deck.Owner])}"
        }
    }
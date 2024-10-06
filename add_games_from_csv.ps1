$games_csv = Get-Content .\games.csv | Select-Object -Skip 1 | ConvertFrom-Csv
$deckList = curl http://localhost:8080/api/decks | ConvertFrom-Json
$decks = @{}
foreach($deck in $deckList) {
    $decks[$deck.name] = $deck.id
}
$pod_id = curl http://localhost:8080/api/pods | ConvertFrom-Json
$pod_id = $pod_id.id
foreach ($game_csv in $games_csv) {
  $participants = 0
  $positions_str = "{"
  if( $game_csv.Deck1 -ne "") {
    $participants++
    $positions_str += """$($decks[$game_csv.Deck1])"": ""$($game_csv.Position1)"","
  }
  if( $game_csv.Deck2 -ne "") {
    $participants++
    $positions_str += """$($decks[$game_csv.Deck2])"": ""$($game_csv.Position2)"","
  }
  if( $game_csv.Deck3 -ne "") {
    $participants++
    $positions_str += """$($decks[$game_csv.Deck3])"": ""$($game_csv.Position3)"","
  }
  if( $game_csv.Deck4 -ne "") {
    $participants++
    $positions_str += """$($decks[$game_csv.Deck4])"": ""$($game_csv.Position4)"","
  }
  if( $game_csv.Deck5 -ne "") {
    $participants++
    $positions_str += """$($decks[$game_csv.Deck5])"": ""$($game_csv.Position5)"","
  }
  if( $game_csv.Deck6 -ne "") {
    $participants++
    $positions_str += """$($decks[$game_csv.Deck6])"": ""$($game_csv.Position6)"","
  }
  $positions_str = $positions_str.TrimEnd(',')
  $positions_str += "}"
  curl -X POST http://localhost:8080/api/games -H "Content-Type: application/json" -d  "{""playedAt"": ""$($game_csv.Date)"", ""placements"": $positions_str, ""participants"": $participants, ""pods"": [$pod_id]}"}
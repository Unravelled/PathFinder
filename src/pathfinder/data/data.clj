(ns pathfinder.data.data)

(defprotocol Data
  (stash! [this data])
  (search [this params]))

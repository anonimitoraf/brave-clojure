(ns brave-clojure.5.pegthing)

;; --- Constructing the board ---

(defn tri*
  "Generates lazy sequence of triangular numbers"
  ([] (tri* 0 1))
  ([sum n]
   (let [new-sum (+ sum n)]
     (cons new-sum (lazy-seq (tri* new-sum (inc n)))))))
(def tri (tri*))

(defn tri?
  "Is the number triangular? e.g. 1, 3, 6, 10, 15, etc"
  [n]
  (= n (comp last take-while)) #(>= n %) tri)

(defn row-tri-end
  "The triangular number at the end of row n"
  [n]
  (last (take n tri)))

(defn row-tri-start
  "The triangular number at the start of row n"
  [n]
  ;; TODO: Behavior is sort of undefined for n < 1 (i.e. just returns n)
  (if (<= n 1)
    n
    (inc (row-tri-end (dec n)))))

(defn row-num
  "Takes a board position and returns the row that it belongs to"
  [pos]
  (inc (count (take-while #(< % pos) tri))))

(defn connect
  "Form a mutual connection between two positions"
  [board max-pos pos1 pos-in-between pos2]
  ;; Check if out-of-bounds, in which case, return board
  (if (> pos1 max-pos)
    board
    (reduce (fn [new-board [src dest]] (assoc-in new-board [src :connections dest] pos-in-between))
            board
            [[pos1 pos2] [pos2 pos1]])))

(defn connect-right
  "Makes a connection from src within its row if within bounds"
  [board max-pos src-pos]
  ;; Check if too close to the edge (i.e. 0 or 1 away),
  ;; if it's the case, make no connections by returning
  ;; the original board
  (let [row (row-num src-pos)
        how-close-to-edge (- (row-tri-end row) src-pos)]
    (if (<= how-close-to-edge 1)
      board
      (let [in-between-pos (inc src-pos)
            dest-pos (inc in-between-pos)]
        (connect board
                 max-pos
                 src-pos
                 in-between-pos
                 dest-pos)))))

(defn calc-offset
  "Given a position and a function to retrieve
  the edge of a row (either start or end), finds
  the offset of the position from that edge"
  [pos row-tri-calc]
  (let [row (row-num pos)]
    (- pos (row-tri-calc row))))

(defn calc-offset-from-left [pos] (calc-offset pos row-tri-start))
(defn calc-offset-from-right [pos] (calc-offset pos row-tri-end))

(defn connect-down
  [board max-pos src-pos offset-calc row-tri-calc]
  (let [src-row (row-num src-pos)
        in-between-row (+ src-row 1)
        dest-row (+ in-between-row 1)
        max-row (row-num max-pos)]
    (if (> dest-row max-row)
      board ;; Out of bounds
      (let [offset-from-edge (offset-calc src-pos)
            dest-pos (+ offset-from-edge (row-tri-calc dest-row))
            in-between-pos (+ offset-from-edge (row-tri-calc in-between-row))]
        (connect board
                 max-pos
                 src-pos
                 in-between-pos
                 dest-pos
                 ))
      )))

(defn connect-down-left
  [board max-pos src-pos]
  (connect-down board max-pos src-pos calc-offset-from-left row-tri-start))

(defn connect-down-right
  [board max-pos src-pos]
  (connect-down board max-pos src-pos calc-offset-from-right row-tri-end))

(defn add-pos
  "Pegs the position and performs connections"
  [board max-pos pos]
  (let [pegged-board (assoc-in board [pos :pegged] true)]
    (reduce (fn [board-so-far connect-fn] (connect-fn board-so-far max-pos pos))
            pegged-board
            [connect-right connect-down-left connect-down-right])))

(defn new-board
  "Creates a new board with the given number of rows"
  [rows]
  (let [initial-board {:rows rows}
        max-pos (row-tri-end rows)]
    (reduce (fn [board pos]
              (add-pos board max-pos pos))
            initial-board
            ;; range is end-exclusive
            (range 1 (inc max-pos)))))

;; --- Interacting with the board ---

(defn change-peg-status
  [board pos is-pegged]
  (assoc-in board [pos :pegged] is-pegged))

(defn place-peg [board pos] (change-peg-status board pos true))
(defn remove-peg [board pos] (change-peg-status board pos false))

(defn multiple-peg-operations
  [board poss operation]
  (reduce (fn [board-so-far pos]
            (operation board-so-far pos))
          board
          poss))

(defn pegged?
  "Does the position have a peg in it?"
  [board pos]
  (get-in board [pos :pegged]))

(defn move-peg
  "Take peg out of pos1 and place it in pos2"
  [board pos1 pos2]
  (place-peg (remove-peg board pos1) pos2))

(defn valid-moves
  "Return a map of all valid moves for pos, where the key is the
  destination and the value is the jumped position"
  [board pos]
  (let [connections (get-in board [pos :connections])
        not-pegged? (complement pegged?)]
    (into {} (filter (fn [[dest-pos in-between-pos]]
                       (and (not-pegged? board dest-pos)
                            (pegged? board in-between-pos)))
                     connections))))
(defn valid-move?
  "Return jumped position if the move from src to dest is valid, nil
  otherwise"
  [board src dest]
  (get (valid-moves board src) dest))

(defn make-move
  "Move peg from src to dest, removing jumped peg"
  [board src dest]
  (if-let [jumped (valid-move? board src dest)]
    (remove-peg (move-peg board src dest) jumped)
    ;; TODO: Maybe it should error if invalid move
    ;; instead of just returning the original board
    board))

;; TODO: Do this properly
(defn get-positions
  [board]
  (sort (filter #(number? %) (keys board))))

(defn has-possible-moves?
    "do any of the pegged positions have valid moves?
    if not, game over"
    [board]
  (let [positions (get-positions board)
        positions-with-moves (filter #(not-empty (valid-moves board %)) positions)]
    ((comp boolean not-empty) positions-with-moves)))

(defn has-possible-moves-2?
  "Do any of the pegged positions have valid moves?
    If not, game over"
  [board]
  (some (comp not-empty (partial valid-moves board))
        (map first (filter #(get (second %) :pegged) board))))

;; --- Displaying board ---
(def alpha-start 97)
(def alpha-end 123)
(def letters (map (comp str char) (range alpha-start alpha-end)))
(def pos-chars 3)

(defn render-pos
  [board pos]
  (str (nth letters (dec pos))
       (if (get-in board [pos :pegged])
         "0"
         "1")))

(defn row-positions
  "Return all positions in the given row"
  [row-num]
  ;; Accounts for the first row which does not have a predecessor
  ;; row
  (let [start (inc (or (row-tri-end (dec row-num)) 0))]
    (range start (inc (row-tri-end row-num)))))

(defn row-padding
  "String of spaces to add to the beginning of a row to center it"
  [row-num total-row-count]
  (let [pad-length (/ (* (- total-row-count row-num) pos-chars) 2)]
    (apply str (take pad-length (repeat " ")))))

(defn render-row
  [board row-num]
  (str (row-padding row-num (:rows board))
       (clojure.string/join " " (map (partial render-pos board) 
                                     (row-positions row-num)))))
(defn print-board
  [board]
  (doseq [row-num (range 1 (inc (:rows board)))]
    (println (render-row board row-num))))

;; Player interaction
(defn letter->pos
  "Converts a letter string to the corresponding position number"
  [letter]
  (inc (- (int (first letter)) alpha-start)))

(defn get-input
  "Waits for user to enter text and hit enter, then cleans the input"
  ([] (get-input nil))
  ([default]
   (let [input (clojure.string/trim (read-line))]
     (if (empty? input)
       default
       (clojure.string/lower-case input)))))

(defn characters-as-strings [char] (str char))

(declare user-entered-valid-move)
(declare user-entered-invalid-move)
(defn prompt-move
  [board]
  (println "\nHere's your board:")
  (print-board board)
  (println "Move from where to where? Enter two letters:")
  (let [input (map letter->pos (characters-as-strings (get-input)))]
    (if-let [new-board (make-move board (first input) (second input))]
      (user-entered-valid-move new-board)
      (user-entered-invalid-move board))))

(defn prompt-empty-peg
  [board]
  (println "Here's your board:")
  (print-board board)
  (println "Remove which peg? [e]")
  (prompt-move (remove-peg board (letter->pos (get-input "e")))))

(defn prompt-rows
  []
  (println "How many rows? [5]")
  (let [rows (Integer. (get-input 5))
        board (new-board rows)]
    (prompt-empty-peg board)))

(defn game-over
  "Announce the game is over and prompt to play again"
  [board]
  (let [remaining-pegs (count (filter :pegged (vals board)))]
    (println "Game over! You had" remaining-pegs "pegs left:")
    (print-board board)
    (println "Play again? y/n [y]")
    (let [input (get-input "y")]
      (if (= "y" input)
        (prompt-rows)
        (do
          (println "Bye!")
          (System/exit 0))))))

(defn user-entered-valid-move
  "Handles the next step after a user has entered a valid move"
  [board]
  (if (has-possible-moves? board)
    (prompt-move board)
    (game-over board)))

(defn user-entered-invalid-move
  "Handles the next step after a user has entered an invalid move"
  [board]
  (println "\n!!! That was an invalid move :(\n")
  (prompt-move board))

;; Tests
(take 3 (next (next (next tri))))
(tri? 2)
(tri? 6)
(row-tri-end 0)
(row-tri-start 5)
(row-num 6)
(connect {} 15 1 2 4)
(connect-right {} 15 4)
(calc-offset-from-left 7)
(connect-down-left {} 15 4)
(connect-down-right {} 15 4)
(add-pos {} 15 4)
(new-board 5)
;; (let [some-board (new-board 5)]
;;   some-board
;;   (println (pegged? some-board 1))
;;   (println (pegged? some-board 16))
;;   )
(def test-board (remove-peg (new-board 5) 4))
(valid-moves test-board 6)
(valid-move? test-board 8 4)
(make-move test-board 1 4)
(make-move test-board 1 5)
;; (has-possible-moves? test-board)
;; (has-possible-moves? (new-board 5))
(print-board test-board)

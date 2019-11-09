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
            board
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

(defn pegged?
  "Does the position have a peg in it?"
  [board pos]
  (get-in board [pos :pegged]))

(defn move-peg
  "Take peg out of pos1 and place it in pos2"
  [board pos1 pos2]
  (place-peg (remove-peg board pos1) pos2))

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

(let [some-board (new-board 5)]
  some-board
  ;; (println (pegged? some-board 1))
  ;;(println (pegged? some-board 16))
  )



; Dianfeng Jiang
; ECS 140a
; Nov 17/2020
;
; Parity-correction
;
;
; main caller
(defun parity-correction (message)
	(intermediatecaller message (nilcounter-in-message message))
)

; intermediate caller to reduce unnessary call
(defun intermediatecaller (message nilnum)
	(lookingforasol message (getUpperBoundofSol nilnum) nilnum)
)


; function that tries all possible solutions
(defun lookingforasol (message tryleft nilnum)
	; try all solution until tryleft reaches 0
	(cond ((< -1 tryleft)
		  	(cond ((parity-check (construc-sol message (matchbilength (decTobi tryleft) nilnum)))
		  			(cons T (construc-sol message (matchbilength (decTobi tryleft) nilnum)))
		  		  )
		  			; try next combine
		  		  (t (lookingforasol message (- tryleft 1) nilnum))
		  	)
		  )
			; if tried all solution but still cant find one
		  (t (cons NIL message))

	)

)



; function that construct one possible solution
(defun construc-sol (message sol)
	(cond ((not (null message))
		  	(cons (construc-sol-row (car message) sol) (construc-sol (cdr message) (nthcdr (nilcounter-in-row (car message)) sol)))
		  )
	)
)



; helper function to get rid of used sol
(defun nilcounter-in-row (word)
	(cond ((null word) 0)
		  ((equal NIL (car word))
		  	(+ 1 (nilcounter-in-row (cdr word)))
		  )
		  (t (nilcounter-in-row (cdr word)))
	)
)


; function that constructs one line of solution
(defun construc-sol-row (word sol)
	(cond ((not (null word))
			(cond ((string= "NIL" (write-to-string (car word)))
				  	(cons (car sol) (construc-sol-row (cdr word) (cdr sol)))
				  )
				  (t (cons (car word) (construc-sol-row (cdr word) sol)))
			)
		  )
	)
)

; get the upper bound of the solution
(defun getUpperBoundofSol (n)
	(- (expt 2 n) 1)
)

; match the bit number of the binarylist to the nil number
(defun matchbilength (binaryList nilnum)
	(cond ((> nilnum (list-length binaryList))
			(cons 0 (matchbilength binaryList (- nilnum 1)))
		  )
		  (t binaryList)

	)
)

; convert decimal number n into binary form
(defun decTobi (n)
	(reverse (decTobi-re n))
)


; convert decimal number n into reversed binary form
(defun decTobi-re(n)
	(cond ((> n 0)
			(cons (nth-value 1 (floor n 2)) (decTobi-re (nth-value 0 (floor n 2))))
		  )
	)
)


; caller function nilcounter-in-message-hp
(defun nilcounter-in-message (message)
	(nilcounter-in-message-hp message 0 0)
)

; helper function count the number of nil in the message
(defun nilcounter-in-message-hp (message row col)
	(cond ((< row (list-length message))
			(cond ((< col (list-length message))
					(cond ((equal NIL (nth col (nth row message)))
							(+ 1 (nilcounter-in-message-hp message row (+ 1 col)))
						  )
						  (t (nilcounter-in-message-hp message row (+ 1 col)))

					)
				  )
				  (t (nilcounter-in-message-hp message (+ 1 row) 0))
			)
		  )
		  (t 0)
	)
)


; Parity check functions
;
; check the parity of the whole message
(defun parity-check (message)
	(and (parity-check-row message) (parity-check-col message))
)

; check if parity in all rows of the message
(defun parity-check-row (message)
	(cond ((null message) T)
		  (t (and (parity-check-list (car message)) (parity-check-row (cdr message))))
	)
)

; check if parity in all columns of the message
(defun parity-check-col (message)
	(parity-check-row (inverse-matrix message))
)

; check if parity in one word list
(defun parity-check-list (word)
	(cond ((= 0 (parityxor word)) T)
		  (t NIL)
	)
)

; xor a list
(defun parityxor (word)
	(cond ((null word) 0)
	      (t (logxor (car word) (parityxor (cdr word))))
	)
)

; inverse one column of the message matrix
(defun construc-col (message row col)
	(cond ((< row (list-length message))
			(cons (nth col (nth row message)) (construc-col message (+ 1 row) col))
		  )
	)
)

; inverse the message matrix so the column parity can be check by the parity-check-row
(defun inverse-matrix (message)
	(inverse-matrix-hp message 0)
)

(defun inverse-matrix-hp (message col)
	(cond ((< col (list-length message))
			(cons (construc-col message 0 col) (inverse-matrix-hp message (+ 1 col)))
	      )
	)
)
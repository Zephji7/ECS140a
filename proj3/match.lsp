; Dianfeng Jiang
; ECS 140a
; Nov 15/2020
;
;Pattern Matching
;
;
; Matching atom
;
; main function to check matching atom
(defun match (pattern assertion)
	; if reach the end of assertion
	(cond ((null assertion)
		   ; if the pattern only have ! left in the pattern
		   (cond ((check-ex pattern) T)
		   	     ; if atom(s) other than ! left in the pattern
		         (t NIL)
		   )
		  )
			; if reach end of pattern
		  ((null pattern) (null assertion))
		  ; neither have reaches the end
		  		; if current pattern atom is !
		  (t (cond ((string=  "!" (string (car pattern)))
		  			; try match the ! with 0 or more atom
			  		(ex-match (cdr pattern) assertion 0)
			     )
				; if current pattern atom is not !
					; check if the current two atom match
		         (t (cond ((match-st (string (car pattern)) (string (car assertion)))
		         		; if two atom match keep going
		         		   (match (cdr pattern) (cdr assertion))
		         		  )
		         		; if not match
		         		  (t NIL)
		         	)
		         )
		     )
		  ) 
	)
)	


; function that verify ! matching atom
(defun ex-match (pattern assertion n_as)
	; while position starts in assertion is less than the length of the assertion
	(cond ((< n_as (list-length assertion))
			; try match pattern start at this position in assertion
			; if succeed, return T
			(cond ((check-ex pattern) T)
				  ((match pattern (nthcdr n_as assertion)) T)
				; if not succeed, try starting at the next position
				  (t (ex-match pattern assertion (+ 1 n_as)))
		    )
		   )
		; if reach the end of assertion but still have not find a match
		  (t NIL)
	)

)


;
;
; Matching string
;
; main function to check matching string
(defun match-st (st_pattern st_assertion)
	; if reach the end of st_assertion
	(cond ((= 0 (length st_assertion))
		   ; if the st_pattern only have * left inside
		   (cond ((check-ast st_pattern) T)
		   	     ; if char other than * left in the st_pattern
		         (t NIL)
		   )
		  )
		  ; if reach end of st_pattern
		  ((= 0 (length st_pattern)) (= 0 (length st_assertion)))
		  ; neither have reaches the end
		  		; if current st_pattern char is *

		  (t (cond ((string=  "*" (subseq st_pattern 0 1))
		  			; try match the * with 0 or more char
			  		(ast-match (subseq st_pattern 1) st_assertion 0)
			     )
				; if current st_pattern char is not *
					; check if the current two char match
		         (t (cond ((string= (subseq st_pattern 0 1) (subseq st_assertion 0 1))
		         		; if two atom match keep going
		         		   (match-st (subseq st_pattern 1) (subseq st_assertion 1))
		         		  )
		         		; if not match
		         		  (t NIL)
		         	)
		         )
		     
		     ) 

	      )
	)
)


; function that verify * matching char
(defun ast-match (st_pattern st_assertion n_st_as)
	; while position starts in st_assertion is less than the length of the assertion
	(cond ((< n_st_as (length st_assertion))
			; try match st_pattern start at this position in st_assertion
			; if succeed, return T
			(cond ((check-ast st_pattern) T)
				  ((match-st st_pattern (subseq st_assertion n_st_as)) T)
				; if not succeed, try starting at the next position
				  (t (ast-match st_pattern st_assertion (+ 1 n_st_as)))
		    )
		   )
		; if reach the end of st_assertion but still have not find a match
		  (t NIL)
	)

)


;
;
; Residual check function
;
; function that verify if the residual in pattern is all !
(defun check-ex (pattern)
	(cond ((null pattern) T)
		  ((string= "!" (string (car pattern)))
		  	(check-ex (cdr pattern))
		  )
		  (t NIL)
	)
)

; function that verify if the residual in st_pattern is all *
(defun check-ast (st_pattern)
	(cond ((= 0 (length st_pattern)) T)
		  ((string= "*" (subseq st_pattern 0 1))
		  	(check-ast (subseq st_pattern 1))
		  )
		  (t NIL)
	)
)

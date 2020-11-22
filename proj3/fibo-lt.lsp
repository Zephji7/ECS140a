; Dianfeng Jiang
; ECS 140a
; Nov 10/2020
;
;Fibonacci number less than N
;
;
(defun fibo-lt (n)
  (reverse (fibo-st n 0) )
)

(defun fibo-st (n l)
  (cond ( (> n (car (fibo-acc l)) )
	 (fibo-st n (+ l 1) ) )
	( t (cdr (fibo-acc l)) )
  )
)

(defun fibo (n)
  (reverse (fibo-acc n) )
)

(defun fibo-acc (n)
  (cond ( (> n -1)
    (cons (nth-value 0 (floor (+ (/ (expt (/ (+ (sqrt 5) 1) 2) n) (sqrt 5)) (/ 1 2) ))) (fibo-acc(- n 1)) ))
  )
)

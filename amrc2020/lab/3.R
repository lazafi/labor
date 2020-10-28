# lab3
load("data/dat.RData")
data <- d[, names(d) %in% c('y', paste0('X', 20:65))]
set.seed(123)
n <- nrow(data)

## 1a)

train <- sample(1:n, round(n*2/3))
test <- (1:n) [-train]
xnames <- paste0('X', setdiff(20:65, 61))
full_formula <- as.formula(paste('y~', paste(xnames, collapse="+")))
model1 <- lm(full_formula, data, subset=train) 
summary (model1)

rtmean <- function(x,trim = 0) {
  x <- sort(x)
  v <- x[1:floor(length(x)*(1-trim))]
  mean(v)
}

#mse_train
rtmean((data[train, 'y'] - predict(model1, data[train,]))^2)
rtmean((data[train, 'y'] - predict(model1, data[train,]))^2, trim=0.1)
#mse_test
rtmean((data[test, 'y'] - predict(model1, data[test,]))^2)
rtmean((data[test, 'y'] - predict(model1, data[test,]))^2, trim=0.1)

## 1b)

library(cvTools)

?cvFit

Y <- data[,'y']
str(Y)

mses <- matrix(ncol=50, nrow=6)
i <- 1
for (k in c(2,5,10,20,50,100)) {
  #print(k)
  ret <- cvFit(lm, formula=full_formula, data=data, y=Y, K=k, R=50, cost=mspe, seed = 123)
  vals <- ret$reps
  print(length(vals))
  mses[i,] <- vals
  #str(ret)
  i <- i + 1
}

loo <- cvFit(lm, formula=full_formula, data=data, y=Y, type="leave-one-out", K=n, cost=mspe, seed = 123)
boxplot(mses[1,], mses[2,], mses[3,], mses[4,], mses[5,], mses[6,], loo$cv, names=c("2","5","10","20","50","100","n"), xlab="K-Fold", ylab="MSE", main="MSEs for CV with 50 Repetitions")

## 1c)


mses <- matrix(ncol=50, nrow=6)
i <- 1
for (k in c(2,5,10,20,50,100)) {
  print(k)
  ret <- cvFit(lm, formula=full_formula, data=data, y=data$y, K=k, R=50, cost=tmspe, costArgs = list(trim = 0.1))
  vals <- ret$reps
  mses[i,] <- vals
  #str(ret)
  i <- i + 1
}

loo <- cvFit(lm, formula=full_formula, data=data, y=Y, type="leave-one-out", K=n, cost=mspe, costArgs = list(trim = 0.1))
boxplot(mses[1,], mses[2,], mses[3,], mses[4,], mses[5,], mses[6,], loo$cv, names=c("2","5","10","20","50","100","n"))

## 1d)

bootsrapf <- function(formula, d, n=1000, trim=0) {
  print(formula)
  #print(d)
  mse_train <- vector() #mse-s on bootraped data
  mse_test <- vector() #mse-s on left-out data
  ltest <- vector() #nr of left-out data samples
  resp <- as.character(formula[[2]])  # extract response variable from formula
  for (m in c(1:n)) {
    bsrp <- sample(1:nrow(d), replace=TRUE)
    #print(bsrp)
    model2 <- lm(formula, data=d[bsrp,])
    mse_train <- append(mse_train, rtmean((d[bsrp, resp] - predict(model2, d[bsrp,]))^2, trim=trim))
    mse_test <- append(mse_test, rtmean((d[-bsrp, resp] - predict(model2, d[-bsrp,]))^2, trim=trim))
    ltest <- append(ltest, nrow(d[-bsrp,]))
  }
  retval <- list("mse_train" = mse_train, "mse_test" = mse_test, "ltest" = ltest)
  return(retval)
}

bs <- bootsrapf(full_formula, data)
boxplot(bs$mse_train, bs$mse_test, names=c("train", "test"))

## 1e)

bs2 <- bootsrapf(full_formula, data, trim=0.1)
boxplot(bs2$mse_train, bs2$mse_test, names=c("train", "test"))


#2

stepwise <- as.formula("y ~ X36 + X64 + X54 + X21 + X37 + X63 + X32 + X45 + 
    X26 + X35 + X22 + X29 + X65 + X34 + X23 + X48 + X28 + X53 + 
    X57 + X62 + X55 + X58")

best_subset_reg <- as.formula("y~X35+X36+X37+X46+X48+X52")

## 2a)

cv1 <- cvFit(lm, formula=stepwise, data=data, y=Y, K=5, R=50, cost=tmspe, costArgs = list(trim = 0.1))
cv2 <- cvFit(lm, formula=best_subset_reg, data=data, y=Y, K=5, R=50, cost=tmspe, costArgs = list(trim = 0.1))
cv1$reps

boxplot(cv1$reps[,1], cv2$reps[,1], names=c("stepwise", "best_subset_reg"))

## 2b)

bs1 <- bootsrapf(stepwise, data, trim=0.1)
bs2 <- bootsrapf(best_subset_reg, data, trim=0.1)
boxplot(bs1$mse_test, bs2$mse_test, names=c("stepwise", "best_subset_reg"))


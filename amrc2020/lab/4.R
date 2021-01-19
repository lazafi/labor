# lab4
load("data/dat.RData")
data <- d
set.seed(1234)
n <- nrow(data)
train <- sample(1:n, round(n*2/3))
test <- (1:n) [-train]

##lab: find constant vals
# with MAD
which(apply(d,2,mad) < 0.01)

## 1a)

model1 <- lm(y~., data, subset=train) 
summary (model1)


rtmean <- function(x,trim = 0) {
  x <- sort(x)
  v <- x[1:floor(length(x)*(1-trim))]
  mean(v)
}

mse <- function(y.true,y.pred, trim=0){
  return(rtmean((y.true - y.pred)^2, trim =trim))
}

rtmean((data[test, 'y'] - predict(model1, data[test,]))^2)
rtmean((data[test, 'y'] - predict(model1, data[test,]))^2, trim=0.1)

plot(data[test, 'y'], predict(model1, data[test,]), xlab='y' ,ylab='y-hat', main="validation")
abline(c(0,1))

## 2a)

library(pls)

ncomp <- 100
model2 <- pcr(y~., ncomp=ncomp, data=data, subset=train, scale=TRUE, validation="CV", segments=10,  segment.type="random")
#summary(model2)

## 2b)
plot(model2, plottype="validation", val.type="MSEP")

selcomp <- 17
predplot(model2, line=TRUE, ncomp=selcomp, labels=rownames(data[train,]))

## 2c)
plot(model2, ncomp=selcomp, line=TRUE, ylim=c(-5,5))

## 2d)

res <- vector(length = ncomp)
for (i in 1:ncomp) {
  res[i] <- mse(data[test,'y'], predict(model2, data[test,], ncomp=i), trim=0.1)
}
plot(res, type='s', xlab="number of components", ylab="TMSE", main="10% trimmed MSE values")

selcomp <- 20

mse(data[test, 'y'], predict(model2, newdata=data[test,], ncomp=selcomp), trim=0.1)

plot(data[test, 'y'], predict(model2, data[test,], ncomp=selcomp), xlab='y' ,ylab='y-hat', main=paste0("validation with components = ", selcomp))
abline(c(0,1))

## 3a)
ncomp <- 100
model3 <- plsr(y~., ncomp=ncomp, data=data, subset=train, scale=TRUE, validation="CV", segments=10,  segment.type="random")


## 3b)
plot(model3, plottype="validation", val.type="MSEP")

## 3c)

predplot(model3, line=TRUE, ncomp=20)

## 3d)

res <- vector(length = ncomp)
for (i in 1:ncomp) {
  res[i] <- mse(data[test,'y'], predict(model3, data[test,], ncomp=i), trim=0.1)
}
plot(res, type='s', xlab="number of components", ylab="TMSE", main="10% trimmed MSE values")

selcomp <- which.min(res)
selcomp
min(res)

plot(data[test, 'y'], predict(model3, data[test,], ncomp=selcomp), xlab='y' ,ylab='y-hat', main=paste0("validation with components = ", selcomp))
abline(c(0,1))

## 3e)

res <- vector(length = ncomp)
for (i in 1:ncomp) {
  res[i] <- mse(data[train,'y'], model3$validation$pred[,1,i], trim=0.1)
}
plot(res, type='s', xlab="number of components", ylab="TMSE", main="10% trimmed MSE values")
selcomp <- which.min(res)
selcomp
min(res)

## 4a)

## lab:
## scale the training set
## then scale the test set with the same mean and var

selcomp = 20
#data.s <- scale(data)
X <- data[train, 2:393]
pc <- princomp(X)
Z <- pc$scores[,1:selcomp]
#y <- scale(data.s[train, 'y'])
y <- data[train, 'y']
betaH <- solve(t(Z)%*%Z)%*%crossprod(Z,y)
yH <- Z %*% betaH
plot(y, yH)
abline(c(0,1))
mse(y, yH, trim=0.1)

#test data
#Xt <- data.s[test, 2:393]
Xt <- scale(data[test, 2:393], center=pc$center, scale=pc$scale)
#yt <- scale(data[test, 'y'], center=y$center, scale=y$scale)
yt <- data[test, 'y']
Zt <- Xt %*% pc$loadings[,1:selcomp]
yH <- Zt %*% betaH
plot(yt, yH)
abline(c(0,1))
mse(yt, yH, trim=0.1)


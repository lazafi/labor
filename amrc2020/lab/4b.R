# lab4
load("data/dat.RData")
data <- d
set.seed(123)
n <- nrow(data)
train <- sample(1:n, round(n*2/3))
test <- (1:n) [-train]

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

plot(data[test, 'y'], predict(model1, data[test,]), xlab='y' ,ylab='y-hat')
abline(c(0,1))

hist(predict(model1, data[test,]))

#ld <- alias(model1,  partial = TRUE)

## 2a)

library(pls)
ncomp <- 300
model2 <- pcr(y~., ncomp=ncomp, data=data, subset=train, scale=TRUE, validation="CV", segments=10,  segment.type="random")
#summary(model2)

## 2b)
plot(model2, plottype="validation", val.type="MSEP")

## 2c)
plot(model2, ncomp=50, line=TRUE)
predplot(model2, line=TRUE, ncomp=50)

plot(model2$validation$adj['y',], type="s", col=2)
str(model2$validation)
plot(model2$validation$PRESS['y',50:60], type="s", col=1)
plot(MSEP(model2))
min(model2$validation$PRESS)

## 2d)

#res <- vector(length = ncomp)
#for (i in 1:ncomp) {
#  res[i] <- mse(data[train,'y'], model2$validation$pred[,'y',i], trim=0.1)
#}
#plot(res, type='s')

res <- vector(length = ncomp)
for (i in 1:ncomp) {
  res[i] <- mse(data[test,'y'], predict(model2, data[test,], ncomp=i), trim=0.1)
}
plot(res, type='s')

mse(data[test, 'y'], predict(model2, newdata=data[test,], ncomp=80), trim=0.1)

plot(data[test, 'y'], predict(model2, data[test,], ncomp=80), xlab='y' ,ylab='y-hat')
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
plot(res, type='s')

plot(data[test, 'y'], predict(model3, data[test,], ncomp=10), xlab='y' ,ylab='y-hat')
abline(c(0,1))

## 3e)

res <- vector(length = ncomp)
for (i in 1:ncomp) {
  res[i] <- mse(data[train,'y'], model3$validation$pred[,1,i], trim=0.1)
}
plot(res, type='s')


## 4a)


ncomp = 80
X <- scale(data[train, !(names(data) %in% 'y')])
#X <- scale(data[train,])
pc <- princomp(X)
str(pc$loadings[,1:80])
#Z <- X %*% pc$loadings[,1:80]
Z <- pc$scores[,1:80]

betaH <- solve(t(Z)%*%Z)%*%crossprod(Z,y)
yH <- Z %*% betaH
y <- data[train, 'y']
plot(y, yH)
abline(c(0,1))
mse(y, yH, trim=0.1)

#test data
#Xt <- scale(data[test, !(names(data) %in% 'y')])
Xt <- scale(data[test,])
yt <- data[test, 'y']
Zt <- Xt %*% pc$loadings[,1:80]
yH <- Zt %*% betaH
plot(yt, yH)
abline(c(0,1))
mse(yt, yH, trim=0.1)



# lab9

# 1)

data(environmental, package="lattice")
str(environmental)

data.ordered <- environmental[order(environmental$temperature),]

cold <- which(data.ordered$temperature <= 85)
warm <- (1:nrow(data.ordered)) [-cold]

plot(data.ordered$temperature, data.ordered$ozone, xlim=c(-10, 100))

smooth <- loess(ozone~temperature, data = data.ordered, subset = cold, control = loess.control(surface = "direct"))
#plot(smooth, xlim=c(50, 100))
lines(smooth$fitted)

pred1 <- predict(smooth, data.ordered[warm,'temperature'], se = TRUE)
pred1
lines(data.ordered[warm,'temperature'], pred1$fit, col = 'red')
lines(data.ordered[warm,'temperature'], pred1$fit + 2 * pred1$se.fit, lty = 2, col = 'red')
lines(data.ordered[warm,'temperature'], pred1$fit - 2 * pred1$se.fit, lty = 2, col = 'red')

# 2)

lecturespl <- function(x, nknots=2, M=4){
    # nknots ... number of knots -> placed at regular quantiles
    # M ... M-1 is the degree of the polynomial
    n <- length(x)
    # X will not get an intercept column
    X <- matrix(NA,nrow=n,ncol=(M-1)+nknots)
    for (i in 1:(M-1)){ X[,i] <- x^i }
    # now the basis functions for the constraints:
    quant <- seq(0,1,1/(nknots+1))[c(2:(nknots+1))]
    qu <- quantile(x,quant)
    for (i in M:(M+nknots-1)){
      X[,i] <- ifelse(x-qu[i-M+1]<0,0,(x-qu[i-M+1])^(M-1))
    }
    list(X=X,quantiles=quant,xquantiles=qu)
}

plot(data$temperature, data$ozone)

spl1 <- lecturespl(data[train, 'temperature'], nknots=3, M=4)
spl1$X
dim(spl1$X)
spl1$xquantiles
#matplot(data$temperature[train], spl1$X, type="l",lty=1, ylim=c(0,1000), xlim=c(50,100))


#plot(spl1$X[,1], spl1$X[,4])
#lines(spl1$X[,1], spl1$X[,5])
#lines(spl1$X[,1], spl1$X[,6])

x <- spl1$X
ozone <- data$ozone[train]
lm1 <- lm(ozone~spl1$X)
str(lm1)
summary(lm1)
lines(data$temperature[train],predict(lm1, newdata=data.frame(x)))
abline(v=spl1$xquantiles)

extraspl2 <- function(x, knots, M=4){
  # nknots ... number of knots -> placed at regular quantiles
  nknots <- length(knots)
  # M ... M-1 is the degree of the polynomial
  n <- length(x)
  # X will not get an intercept column
  X <- matrix(NA,nrow=n,ncol=(M-1)+nknots)
  for (i in 1:(M-1)){ X[,i] <- x^i }
  # now the basis functions for the constraints:
  #quant <- seq(0,1,1/(nknots+1))[c(2:(nknots+1))]
  qu <- knots
  for (i in M:(M+nknots-1)){
    X[,i] <- ifelse(x-qu[i-M+1]<0,0,(x-qu[i-M+1])^(M-1))
  }
  list(X=X,xquantiles=qu)
}

extraspl <- function(x, knots, M=4){
  # nknots ... number of knots -> placed at regular quantiles
  nknots <- length(knots)
  # M ... M-1 is the degree of the polynomial
  x <- c(x, seq(86,137,1))
  n <- length(x)
  # X will not get an intercept column
  X <- matrix(NA,nrow=n,ncol=(M-1)+nknots)
  for (i in 1:(M-1)){ X[,i] <- x^i }
  # now the basis functions for the constraints:
  #quant <- seq(0,1,1/(nknots+1))[c(2:(nknots+1))]
  qu <- knots
  for (i in M:(M+nknots-1)){
    X[,i] <- ifelse(x-qu[i-M+1]<0,0,(x-qu[i-M+1])^(M-1))
  }
  list(X=X,xquantiles=qu)
}


spl3 <- extraspl(data[, 'temperature'], knots=spl1$xquantiles, M=4)
spl3

ozone3 <- data$ozone[test]
x3 <- spl3$X
lm3 <- lm(ozone~spl3$X)
lines(data$temperature, predict(lm3, newdata=data.frame(data$temperature)),col='red')
lines(data$temperature, predict(lm3, newdata=data.frame(data$temperature)),col='red')
abline(v=spl1$xquantiles, col='red')

matplot(data$temperature, spl3$X, type="l",lty=1, ylim=c(0,1000))


# 3)

library(splines)


bs1 <- lm(ozone ~ bs(temperature, df=3), data=data, subset=train)
plot(data$temperature, data$ozone, ylim=c(0,300))
lines(data$temperature[train], predict.lm(bs1, data.frame(temperature=data$temperature[train])))
lines(data$temperature[test], predict.lm(bs1, data.frame(temperature=data$temperature[test])), col="red")

matplot(data$temperature[train], bs(data$temperature[train], df=3), type="l",lty=1)

# 4)

ns1 <- lm(ozone ~ ns(temperature, df=3), data=data, subset=train)
plot(data$temperature, data$ozone)
lines(data$temperature[train], predict.lm(ns1))
lines(data$temperature[test], predict.lm(ns1, data.frame(temperature=data$temperature[test])), col="red")

matplot(data$temperature[train], ns(data$temperature[train], df=3), type="l",lty=1)



#lines(data.ordered[cold, 'temperature'], predict(lm1, newdata=data.ordered[cold, 'temperature']))
#plot(lm1)
plot(data.ordered$temperature, data.ordered$ozone, xlim=c(50, 100))
predict(lm1)
lines(predict(lm1))
lines(data.ordered$temperature, predict(lm1, newdata=data.ordered$temperature))

plot(data.ordered[warm, 'temperature'], predict(lm1, newdata=data.ordered[warm,]))
plot(seq(1,nrow(data.ordered[warm,]),1), predict(lm1, newdata=data.ordered[warm,]))









x <- seq(1,10,length=100)
y <- sin(x) + 0.1 * rnorm(x)
x1 <- seq(-1,12,length=100)
plot(x, y, xlim = range(x1))

spl <- lecturespl(x, nknots=2, M=4)
lm1 <- lm(y ~ spl$X)
lines(x, predict(lm1, newdata=data.frame(x)), col="blue")
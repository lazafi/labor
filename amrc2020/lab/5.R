# lab5
load("data/dat.RData")
data <- d
set.seed(1234)
n <- nrow(data)
train <- sample(1:n, round(n*2/3))
test <- (1:n) [-train]

# baseline


rtmean <- function(x,trim = 0) {
  x <- sort(x)
  v <- x[1:floor(length(x)*(1-trim))]
  mean(v)
}

mse <- function(y.true,y.pred, trim=0){
  return(rtmean((y.true - y.pred)^2, trim =trim))
}


model1 <- lm(y~., data, subset=train) 

rtmean((data[test, 'y'] - predict(model1, data[test,]))^2)
rtmean((data[test, 'y'] - predict(model1, data[test,]))^2, trim=0.1)


# 1a)

library(MASS)
lambda <- seq(1, 100, 0.1)
model2 <- lm.ridge(y~., data, subset=train, lambda = lambda)

# minimal cgv
min(model2$GCV)
# at lambda
model2$lambda[which.min(model2$GCV)]

lambda.opt <- model2$lambda[which.min(model2$GCV)]
plot(model2)
abline(v=lambda.opt, lty=2)
plot(lambda, model2$GCV, type='l')
abline(v=lambda.opt, lty=2)


# 1b)

model2.sel <- lm.ridge(y~., data, subset=train, lambda = lambda.opt)
coef.model2.sel <- coef(model2.sel)

# 1c)

y.pred <- as.matrix(cbind(rep(1,length(test)), data[test,-1])) %*% coef.model2.sel
plot(data[test,'y'], y.pred)
abline(c(0,1))

mse(data[test, 'y'], y.pred, trim=0.1)


# 1d)

vars <-  which(apply(d,2,mad) < 0.001)
for (x in seq_along(vars)) {
  print(x)
  hist(data[,vars[x]], main=names(vars)[x])
}

data.clean <- data[,(!names(data) %in% c('X177', 'X201', 'X261', '276'))]
names(data.clean)
model3 <- lm.ridge(y~., data.clean, subset=train, lambda = lambda)

# minimal cgv
min(model3$GCV)
# at lambda
model3$lambda[which.min(model3$GCV)]

lambda.opt <- model3$lambda[which.min(model3$GCV)]
plot(model3)
abline(v=lambda.opt, lty=2)

plot(lambda, model3$GCV, type='l')
abline(v=lambda.opt, lty=2)

model3.sel <- lm.ridge(y~., data.clean, subset=train, lambda = lambda.opt)
coef.model3.sel <- coef(model3.sel)
y.pred <- as.matrix(cbind(rep(1,length(test)), data.clean[test,-1])) %*% coef.model3.sel
plot(data.clean[test,'y'], y.pred)
abline(c(0,1))

mse(data.clean[test, 'y'], y.pred, trim=0.1)

# 2a)

library(glmnet)

?glmnet
model4 <- glmnet(as.matrix(data.clean[train, -1]), data.clean[train, 1])
print(model4)
plot(model4)
str(model4)
plot(model4$lambda)

# 2b)

model4.cv <- cv.glmnet(as.matrix(data.clean[train, -1]), data.clean[train, 1])
str(model4.cv)
plot(model4.cv)
model4.cv$lambda.min
model4.cv$lambda.1se

coef(model4.cv, s="lambda.1se") [which(coef(model4.cv, s="lambda.1se") != 0),]

# 2c)

y.pred <- predict(model4.cv, newx=as.matrix(data.clean[test,-1]), s="lambda.1se")
plot(data.clean[test,'y'], y.pred)
abline(c(0,1))

mse(data.clean[test, 'y'], y.pred, trim=0.1)


y.pred <- predict(model4.cv, newx=as.matrix(data.clean[test,-1]), s="lambda.min")
plot(data.clean[test,'y'], y.pred)
abline(c(0,1))

mse(data.clean[test, 'y'], y.pred, trim=0.1)

#y.pred <- as.matrix(cbind(rep(1,length(test)), data.clean[test,-1])) %*% coef(model4.cv, s="lambda.1se") 
coef(model4.cv, s="lambda.1se")



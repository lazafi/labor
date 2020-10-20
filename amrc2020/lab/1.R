#install.packages("ISLR")
data(Hitters,package="ISLR")
data <- na.omit(Hitters)
str(data)
head(data)

# factorial variables should be dummy-encoded
# vars with skewed distributions could be log-transfered

# separate data in train and test
set.seed(123)
train_i <- sample(seq_len(nrow(data)), size = floor(2/3 * nrow(data)))
train <- data[train_i, ]
test <- data[-train_i, ]

#1a

y <- train$Salary
one <- rep(1, nrow(train))
x <- data.matrix(train[, !names(train) %in% c("Salary")])
X <- cbind(one, x)
str(X)
thetaH <- solve(t(X) %*% X) %*% t(X) %*% y
str(thetaH)
yH <- X %*% thetaH
str(yH)
plot(y, yH)
abline(1,1)


#1b

X2 <- model.matrix(Salary~., data=train)
str(X2)
thetaH2 <- solve(t(X2) %*% X2) %*% t(X2) %*% y
str(thetaH2)
thetaH2
yH2 <- X2 %*% thetaH2
str(yH2)
plot(y, yH2)
abline(1,1)

#becouse factor variables are encoded as 0/1, intercept is different (level0 is included in the intercept)

#1c

lm0 <- lm(Salary~., data=data, subset=train_i)
summary(lm0)

#1d

yh_c1 <- predict(lm0, train)
plot(train$Salary, yh_c1) 
abline(1,1)

yh_c2 <- predict(lm0, test)
plot(test$Salary, yh_c2) 
abline(1,1)

#1e

mse_train <- mean((y - yh_c1)^2)
mse_test <- mean((test$Salary - yh_c2)^2)

#2a
lm2 <- lm(Salary~Walks+DivisionW+PutOuts+Assists+Errors+1, train)

summary(lm2)
x <- data.matrix(train[, names(train) %in% c("Walks", "Division", "PutOuts", "Assists", "Errors")])
X <- cbind(one, x)
#X <- x
str(X)
thetaH <- solve(t(X) %*% X) %*% t(X) %*% y
str(thetaH)
yH <- X %*% thetaH
str(yH)

#2b

plot(y, yH)
abline(1,1)

x <- data.matrix(test[, names(test) %in% c("Walks", "Division", "PutOuts", "Assists", "Errors")])
one <- rep(1, nrow(test))
X <- cbind(one, x)
yh_c2 <- X %*% thetaH
plot(test$Salary, yh_c2) 
abline(1,1)

#2c

mse_test <- mean((test$Salary - yh_c2)^2)


#2d
redmodel <- lm(Salary~Walks+Division+PutOuts+Assists+Errors+1, train)
summary(redmodel)

anova(redmodel, lm0)
# small model first

###ex
# transform Salary

model2 <- lm(log(Salary)~Walks+Division+PutOuts+Assists+Errors+1, train)
summary(model2)
plot(log(train$Salary), predict(model2, train)) 
plot(log(test$Salary), predict(model2, test)) 



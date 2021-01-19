# lab 10

data(Auto, package="ISLR")

set.seed(123)
n <- nrow(Auto)
train <- sample(1:n, round(n*2/3))
test <- (1:n) [-train]


str(Auto)
#summary(Auto)

library(splines)
plot(Auto$displacement, Auto$mpg)
plot(Auto$cylinders, Auto$mpg)
plot(Auto$horsepower, Auto$mpg)
plot(Auto$weight, Auto$mpg)
plot(Auto$acceleration, Auto$mpg)
plot(Auto$year, Auto$mpg)
plot(Auto$origin, Auto$mpg)

#1)

model1 <- lm(mpg~ns(displacement, 4) + ns(horsepower, 4) + ns(acceleration, 4) + ns(weight, 4) + origin + ns(year,4) + cylinders, data=Auto, subset=train)

# a)
summary(model1)

pred1 <- predict(model1, Autbo[test,])
plot(pred1, Auto[test,'mpg'])
abline(c(0,1))

sqrt(mean((Auto[test, 'mpg'] - predict(model1, Auto[test,]))^2))

# b)

model2 <- step(lm(mpg~ns(displacement, 4) + ns(horsepower, 4) + ns(acceleration, 4) + ns(weight, 4) + origin+ ns(year,4)+cylinders, Auto, train), direction='both',trace=0)
summary(model2)

sqrt(mean((Auto[test, 'mpg'] - predict(model2, Auto[test,]))^2))

# c)
model2$coefficients
plot(Auto$horsepower[train], model2$model$`ns(horsepower, 4)` %*% model2$coefficients[2:5])
plot(Auto$acceleration[train], model2$model$`ns(acceleration, 4)` %*% model2$coefficients[6:9])
plot(Auto$weight[train], model2$model$`ns(weight, 4)` %*% model2$coefficients[10:13])
plot(Auto$year[train], model2$model$`ns(year, 4)` %*% model2$coefficients[15:18])

# 2)

# a)

library(mgcv)


model3 <- gam(mpg~s(displacement) + s(horsepower) + s(acceleration) + s(weight) + origin + s(year) + cylinders, data=Auto, subset=train,  family = gaussian)
summary(model3)


# b)

plot(model3, page=1,shade=TRUE,shade.col = "yellow")

# c)

sqrt(mean((Auto[test, 'mpg'] - predict(model3, Auto[test,]))^2))

# d)

model4 <- gam(mpg~s(displacement) + s(horsepower) + s(acceleration) + s(weight) + origin + s(year) + cylinders, data=Auto, subset=train, select = TRUE, family = gaussian)
summary(model4)
plot(model4, page=1,shade=TRUE,shade.col = "yellow")
sqrt(mean((Auto[test, 'mpg'] - predict(model4, Auto[test,]))^2))

model5 <- gam(mpg~s(displacement) + s(horsepower) + s(acceleration) + s(weight) + origin + s(year, k=3) + cylinders, data=Auto, subset=train, family = gaussian)
plot(model5, page=1,shade=TRUE,shade.col = "yellow")
summary(model5)
sqrt(mean((Auto[test, 'mpg'] - predict(model5, Auto[test,]))^2))

model7 <- gam(mpg~s(displacement,bs='ts') + s(horsepower,bs='ts') + s(acceleration,bs='ts') + s(weight,bs='ts') + origin + s(year,bs='ts') + cylinders, data=Auto, subset=train, family = gaussian)
plot(model7, page=1,shade=TRUE,shade.col = "yellow")
summary(model7)
sqrt(mean((Auto[test, 'mpg'] - predict(model7, Auto[test,]))^2))


model8 <- gam(mpg~s(displacement,bs='cr') + s(horsepower,bs='cr') + s(acceleration,bs='cr') + s(weight,bs='cr') + origin + s(year,bs='cr') + cylinders, data=Auto, subset=train, family = gaussian, method='REML')
plot(model8, page=1,shade=TRUE,shade.col = "yellow")
summary(model8)
sqrt(mean((Auto[test, 'mpg'] - predict(model8, Auto[test,]))^2))

model9 <- gam(mpg~s(displacement,bs='cr') + s(horsepower,bs='cr') + s(acceleration,bs='cr') + s(weight,bs='cr') + origin + s(year,bs='cr') + cylinders, data=Auto, subset=train, family = gaussian, select = TRUE, method='REML')
plot(model9, page=1,shade=TRUE,shade.col = "yellow")
summary(model9)
sqrt(mean((Auto[test, 'mpg'] - predict(model9, Auto[test,]))^2))

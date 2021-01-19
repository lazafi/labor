# lab2
load("data/dat.RData")
str(d)
head(d)

data <- d[, names(d) %in% c('y', paste0('X', 20:65))]
hist(data$y)
set.seed(123)
n <- nrow(data)
train <- sample(1:n, round(n*2/3))
test <- (1:n) [-train]

# 1)

full_formula <- as.formula(paste('y~', paste(paste0('X', 20:65), collapse="+")))
model1 <- lm(full_formula, data, subset=train) 
summary (model1)
alias(model1,  partial = TRUE)

plot (data$X21, data$X61)

reducedf1 <-as.formula(paste('y~', paste(paste0('X', setdiff(20:65, 61)), collapse="+")))
#model2 <- lm(reducedf1, data, subset=train) 
model2 <- update(model1, .~.-X61)
summary(model2)

#train <- data[train_i, ]
#test <- data[-train_i, ]

alias(model2,  partial = TRUE)

plot(data[train, 'y'], predict(model2, data[train,]), xlab='y' ,ylab='y-hat')
abline(c(0,1))
plot(data[test, 'y'], predict(model2, data[test,]), xlab='y' ,ylab='y-hat')
abline(c(0,1))

#mse_train
mean((data[train, 'y'] - predict(model2, data[train,]))^2)
#mse_test
mean((data[test, 'y'] - predict(model2, data[test,]))^2)

# 2)

model3 <- step(lm(y~1,data, train), scope=full_formula, direction='forward', trace=0)

model4 <- step(lm(full_formula, data, train), direction='backward',trace=0)

#model5 <- step(lm(full_formula, data, train), trace=0)

model5 <- step(lm(y~1,data, train), scope=full_formula, direction='both', trace=0)
## trace=0 # no output

summary(model3) #22
summary(model4) #24
summary(model5) #24

anova(model3, model4, model5)
## models not neccesarly nested -> anove not well suitable
#model4 == model5


#plot(data[train, 'y'], predict(model3, data[train, ]), xlab='y' ,ylab='y-hat')
#abline(c(0,1))
plot(data[test, 'y'], predict(model3, data[test, ]), xlab='y' ,ylab='y-hat')
abline(c(0,1))
#mse_train
mean((data[train, 'y'] - predict(model3, data[train,]))^2)
#mse_test
mean((data[test, 'y'] - predict(model3, data[test,]))^2)

plot(data[test, 'y'], predict(model4, data[test, ]), xlab='y' ,ylab='y-hat')
abline(c(0,1))
#mse_train
mean((data[train, 'y'] - predict(model4, data[train,]))^2)
#mse_test
mean((data[test, 'y'] - predict(model4, data[test,]))^2)

# 3a)

library(leaps)

model.rs <- regsubsets(reducedf1, data=data, subset=train, nbest=3, really.big=TRUE, nvmax=10)
plot(model.rs)
summary(model.rs)
# 3b)



# 3c)

s <- summary(model.rs)

str(s)

plot(1:length(s$bic), s$bic)

bestformula <- paste0("y~", paste(setdiff(names(which(s$which[18,])), '(Intercept)'), collapse = '+'))

model.best <- lm(bestformula, data, train)
summary(model.best)

plot(data[test, 'y'], predict(model.best, data[test, ]), xlab='y' ,ylab='y-hat')
abline(c(0,1))
#mse_test
mean((data[test, 'y'] - predict(model.best, data[test,]))^2)


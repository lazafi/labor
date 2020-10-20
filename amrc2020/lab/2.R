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

#reducedf1 <-as.formula(paste('y~', paste(paste0('X', setdiff(20:65, 61)), collapse="+")))
#model2 <- lm(reducedf1, data, subset=train) 
model2 <- update(model1, .~.-X61)
summary(model2)

#train <- data[train_i, ]
#test <- data[-train_i, ]

alias(model2,  partial = TRUE)

plot(data[train, 'y'], predict(model2, data[train,]))
abline(c(0,1))
plot(data[test, 'y'], predict(model2, data[test,]))
abline(c(0,1))

#mse_train
mean((data[train, 'y'] - predict(model2, data[train,]))^2)
#mse_test
mean((data[test, 'y'] - predict(model2, data[test,]))^2)

# 2)

step(lm(y~1, train), scope=full_formula)


model3 <- lm(formula = y ~ X36 + X64 + X54 + X21 + X37 + X63 + X32 + X45 + X26 + X35 + X22 + X29 + X65 + X34 + X23 + X48 + X53 + X57 + X62 + X55 + X58, data = train)
summary(model3)

plot(train$y, predict(model3, train))
abline(1,1)
plot(test$y, predict(model3, test))
abline(1,1)

#mse_train
mean((train$y - predict(model3, train))^2)
#mse_test
mean((test$y - predict(model3, test))^2)


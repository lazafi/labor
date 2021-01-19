# lab 11

data <- read.csv2("data/winequality-red.csv", na.strings="", dec=".", skipNul=TRUE)
str(data)
summary(data)

set.seed(1234)
n <- nrow(data)
train <- sample(1:n, round(n*2/3))
test <- (1:n) [-train]

#install.packages("rpart")
library(rpart)
?rpart

t0 <- rpart(quality~., data, subset=train)
t0
plot(t0, unitform=TRUE, branch=1, compress=TRUE)
text(t0, use.n = TRUE)

# c)


#mse_train
sqrt(mean((data[train, 'quality'] - predict(t0, data[train,]))^2))
#mse_test
sqrt(mean((data[test, 'quality'] - predict(t0, data[test,]))^2))

# d)

#printcp(t0)
cp
plotcp(t0)
str(cp)
cp[3, 'CP']

# e)

?prune
t1 <- prune(t0, cp[5, 'CP'])
plot(t1, branch=1, compress=TRUE)
text(t1, use.n = TRUE)

# f)

#mse_train
sqrt(mean((data[train, 'quality'] - predict(t1, data[train,]))^2))
#mse_test
sqrt(mean((data[test, 'quality'] - predict(t1, data[test,]))^2))

# g)


B <- 100
for (k in seq(1,B)) {
  tr <- sample(train, nrow(data), replace=TRUE)
  tree <- rpart(quality~., data, subset=tr)

  pred <- predict(tree, data[test,])
  if (! exists("pred_avg")) {
    pred_avg <- pred
  }
  pred_avg <- (pred + k*pred_avg) / (k+1)
}
pred_avg
rmse <- sqrt(mean((data[test, 'quality'] - pred_avg )^2))
print(rmse)  

# h)


mergeavg <- function(src, new) {
  for (i in names(new)) {
    if (is.na(src[i,'v'])) {
      src[i, 'v'] <- new[i]
      src[i,'n'] <- 1
    } else {
      src[i, 'v'] <- (new[i] + src[i, 'n'] * src[i, 'v']) / (src[i, 'n'] + 1)
      src[i, 'n'] <- src[i, 'n'] + 1
    }
  }
  src
}


B <- 100

pred_avg <- array(numeric(0),dim=c(nrow(data), 2))
allidx <- seq(1,nrow(data))
dimnames(pred_avg) <- list(allidx, c('n','v'))
str(pred_avg)

for (k in seq(1,B)) {
  tr <- sample(train, nrow(data), replace=TRUE)
  oob <- data[-tr,]
  tree <- rpart(quality~., data, subset=tr)
  
  pred <- predict(tree, oob)
  str(pred)
  pred_avg <- mergeavg(pred_avg, pred)
}

mse_oob <- sum((data$quality - pred_avg[, 'v'])^2)/nrow(data)
mse_oob


rmse <- sum(data[train, 'quality'] - pred_avg)^2

rmse_oob <- sqrt(mean((oob$quality -  predict(tree, oob))^2))
rmses[k] <- rmse_oob  

rmses

mean(rmses)

# i)

#install.packages("randomForest")
library(randomForest)
rf <- randomForest(quality~., data=data, subset=train, importance=TRUE)
pred <- predict(rf,data[test,])
sqrt(mean((data[test, 'quality'] -  pred)^2))



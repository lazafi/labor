#lab7

#1)

#install.packages("rrcov")
data(olitos, package="rrcov")

str(olitos)
summary(olitos)
X <- olitos[,-26]

set.seed(1234)
n <- nrow(olitos)
train <- sample(1:n, round(n*2/3))
test <- (1:n) [-train]

#a)

library(MASS)
model1 <- lda(grp~., data=olitos, subset=train)
pred1 <- predict(model1, olitos[test,])
T <- table(olitos[test,'grp'], pred1$class)
e1 <- 1-sum(diag(T))/sum(T)
e1

model1.cv <- lda(grp~., data=olitos, subset=train, CV=TRUE)
T <- table(olitos[train,'grp'], model1.cv$class)
e1.cv <- 1-sum(diag(T))/sum(T)
e1.cv



#b)

## lab: there must be more samples in each class then the nr of X
##  possible solution: use pca to reduce the x variables

table(olitos[train,"grp"])

olitos.1 <- sample(which(olitos$grp == 1), 500, replace=TRUE)
olitos.2 <- sample(which(olitos$grp == 2), 250, replace=TRUE)
olitos.3 <- sample(which(olitos$grp == 3), 340, replace=TRUE)
olitos.4 <- sample(which(olitos$grp == 4), 110, replace=TRUE)

olitos.big <- olitos[c(olitos.1, olitos.2, olitos.3, olitos.4),]

train.b <- sample(1:n, round(n*4/5))
test.b <- (1:n) [-train.b]

model2 <- qda(grp~., data=olitos.big)
pred2 <- predict(model2, olitos[test,])
table(pred2$class, olitos[test,'grp'])

#1c)

library(klaR)

model3 <- rda(grp~., data=olitos, subset=train)
model3$regularization

pred3 <- predict(model3, olitos[test,])
T <- table(olitos[test,'grp'], pred3$class)f
e3 <- 1-sum(diag(T))/sum(T)
e3



#2)

bank <- read.csv2("data/bank.csv")

set.seed(1234)
train <- sample(1:nrow(bank), 3000)
test <- (1:nrow(bank)) [-train]

#a)

model2.1 <- lda(y~., data=bank, subset=train)
pred2.1 <- predict(model2.1, bank[test,])
T <- table(bank[test,'y'], pred2.1$class)
T
e4 <- 1-sum(diag(T))/sum(T)
e4
model2.1$prior

#b)

f <- 0.2
model2.2 <- lda(y~., data=bank, subset=train, prior=c(f, 1-f))
pred2.2 <- predict(model2.2, bank[test,])
T <- table(bank[test,'y'], pred2.2$class)
T
e5 <- 1-sum(diag(T))/sum(T)
e5

specificity <- array()
accuracy <- array()
i <- array()

for (x in c(seq(0.1, 0.49, 0.1), seq(0.51, 1, 0.1))) {
  
  model <- lda(y~., data=bank, subset=train, prior=c(x, 1-x))
  pred <- predict(model, bank[test,])
  T <- table(bank[test,'y'], pred$class)
  mr <- 1-sum(diag(T))/sum(T)

  specificity <- c(specificity, T[2,2] / (T[2,1] + T[2,2]))
    accuracy <- c(accuracy, mr)
    i <- c(i, x)
    print(i)
  }

plot(i, accuracy, type='l', ylim=c(0,1), col="black", ylab="Missclassification and Sensitivity", main="Tuning the model with constant i")
lines(i, specificity, type='l', col="green")
legend("bottomright", legend=c('missclassification rate','sensitivity'), col = c("black", "green"), pch = "---")

##lab: in addition to prior changing we can also do under/oversampling




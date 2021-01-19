# lab6

#1a)

#install.packages("rrcov")
data(olitos, package="rrcov")

str(olitos)
summary(olitos)
X <- olitos[,-26]

##lab: scaling important !
pc <- princomp(scale(X), scores = TRUE)
str(pc)
plot(pc$scores[,1], pc$scores[,2], col=olitos$grp)

#1b)


#### bsp from lecture
grp <- c(rep(1,100),rep(2,100))
g1 <- matrix(rnorm(2*100,0.1), ncol=2)
g2 <- matrix(rnorm(2*100,4.1), ncol=2)
x <- rbind(g1,g2)

y1 <- c(rep(1,100), rep(0,100))
y2 <- c(rep(0,100), rep(1,100))
y <- cbind(y1, y2)

d <- cbind(y,x)
str(d)
res.lm <- lm(y~x)
summary(res.lm)



####

set.seed(1234)
n <- nrow(olitos)
train <- sample(1:n, round(n*2/3))
test <- (1:n) [-train]

response <- cbind(ifelse(olitos$grp==1, 1, 0), ifelse(olitos$grp==2, 1, 0), ifelse(olitos$grp==3, 1, 0), ifelse(olitos$grp==4, 1, 0))
data <- olitos[,-26]

model1 <- lm(response~., data=data, subset=train)
summary(model1)

pred.y <- predict(model1, data[test,])
pred <- apply(pred.y, 1, which.max)
confmat1 <- table(pred, olitos[test,'grp'])
confmat1

sum(diag(confmat1))/sum(confmat1)
#2a)

bank <- read.csv2("data/bank.csv")
#bank <- rbind(bank, bank[which(bank$y == "yes"),])

set.seed(1234)
train <- sample(1:nrow(bank), 3000)
test <- (1:nrow(bank)) [-train]

x <- bank[,-17]
y1 <- ifelse(bank$y==levels(bank$y)[1], 1, 0)
y2 <- ifelse(bank$y==levels(bank$y)[2], 1, 0)
data <- cbind(x,y1,y2)
model2 <- lm(cbind(y1,y2)~., data=data, subset=train)


pred.num <- predict(model2, data[test,])
pred <- ifelse(apply(pred.num, 1, which.max) == 1, "no", "yes")
confmatrix <- table(bank[test,'y'], pred)
fnr <- confmatrix[2,1] / (confmatrix[2,1] + confmatrix[2,2])
recal <- confmatrix[2,2] / (confmatrix[2,2] + confmatrix[2,1])
fnr
confmatrix

# sample more of 'y'

bsrp.y <- sample(which(data[train, "y1"] == 1), 1000, replace=TRUE)
bsrp.n <- sample(which(data[train, "y1"] == 0), 1000, replace=TRUE)
bsrp <- as.vector(rbind(bsrp.y,bsrp.n))

model2b <- lm(cbind(y1,y2)~., data=data, subset=bsrp)

pred.num <- predict(model2b, data[test,])
pred <- ifelse(apply(pred.num, 1, which.max) == 1, "no", "yes")
confmat2b <- table(bank[test,'y'], pred)
confmat2b
paste0((1-sum(diag(confmat2b))/sum(confmat2b)) * 100, "%")


#find best tuning param

recal <- array()
precision <- array()
accuracy <- array()
i <- array()

for (x in seq(0, 2, 0.1)) {
  ###lab: never use test set for tuning the classifier
  #pred.num <- predict(model2, data[test,])
  pred.num <- predict(model2, data[train,])
  pred.num[,2] <- pred.num[,2] + x
  pred <- ifelse(apply(pred.num, 1, which.max) == 1, "no", "yes")
  #confmatrix <- table(bank[test,'y'], pred) 
  confmatrix <- table(bank[train,'y'], pred) 
  recal <- c(recal, confmatrix[2,2] / (confmatrix[2,2] + confmatrix[2,1]))
  precision <- c(precision, confmatrix[2,2] / (confmatrix[2,2] + confmatrix[1,2]))
  accuracy <- c(accuracy, (confmatrix[1,1] + confmatrix[2,2]) / sum(confmatrix))
  i <- c(i, x)
}

plot(i, accuracy, type='l', ylim=c(0,1), col="black")
lines(i, recal, type='l', col="red")
lines(i, precision, type='l', col="blue")

#baseline
b <- which.max(table(data[,'y1']))
base.accuracy <- max(table(data[train,'y1'])) / length(train)
abline(c(base.accuracy,0), lty=2)

base.recal <- max(table(data[train,'y1'])) / length(train)

legend("topright", legend=c('accuracy','precision','recal'), col = c("red", "blue", "black"))


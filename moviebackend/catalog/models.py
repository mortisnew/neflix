from django.db import models

class Country(models.Model):
    name = models.CharField(max_length=100)
    def __str__(self):
        return self.name

class Genre(models.Model):
    name = models.CharField(max_length=100)
    def __str__(self):
        return self.name

class Language(models.Model):
    name = models.CharField(max_length=100)
    def __str__(self):
        return self.name

class Quality(models.Model):
    name = models.CharField(max_length=100)
    def __str__(self):
        return self.name

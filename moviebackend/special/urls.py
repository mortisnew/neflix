from rest_framework import routers
from django.urls import path,include
from . import views

router = routers.DefaultRouter()
router.register(r'favorites', views.FavoriteViewSet, basename='favorite')
router.register(r'watchlist', views.WatchlistViewSet, basename='watchlist')
router.register(r'recently-watched', views.RecentlyWatchedViewSet, basename='recently_watched')
router.register(r'recently-episodes', views.RecentlyEpisodeViewSet, basename='recently_episode')
router.register(r'folders', views.FolderViewSet, basename='folder')
router.register(r'folder-item', views.FolderItemViewSet, basename='folder_Item')

urlpatterns =[
    path('', include(router.urls)),
    path('search/', views.SearchView.as_view(), name='search'),
    path('recommendation/', views.RecommendationViewSet.as_view(), name='recommendation'),
]